package com.song.spring.boot.fileserver.controller;

import com.song.spring.boot.fileserver.domain.File;
import com.song.spring.boot.fileserver.service.FileService;
import com.song.spring.boot.fileserver.util.MD5Util;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600) // 允许所有域名访问
@Controller
public class FileController {

	private  final Long maxSize = 16777216L;

	@Autowired
	private FileService fileService;

	@Value("${server.address}")
	private String serverAddress;

	@Value("${server.port}")
	private String serverPort;

	@RequestMapping(value = "/")
	public String index(Model model) {
		// 展示最新二十条数据
		model.addAttribute("files", fileService.listFilesByPage(0, 20));
		return "index";
	}

	/**
	 * 分页查询文件
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@GetMapping("files/{pageIndex}/{pageSize}")
	@ResponseBody
	public List<File> listFilesByPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
		return fileService.listFilesByPage(pageIndex, pageSize);
	}

	/**
	 * 获取文件片信息
	 * 
	 * @param id
	 * @return
	 * @throws UnsupportedEncodingException 
	 */

	@GetMapping("files/{id}")
	@ResponseBody
	public ResponseEntity<Object> serveFile(@PathVariable String id, HttpServletResponse response) throws Exception {
		Optional<File> file = fileService.getFileById(id);
		if(!file.isPresent()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("<h1>File was not fount</h1>");
		}
		File myFile = file.get();

		ResponseEntity.BodyBuilder header = ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + new String(file.get().getName().getBytes("utf-8"), "ISO-8859-1"))
				.header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
				.header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close");

		if(myFile.getContent() != null){
		return header.body(myFile.getContent().getData());
		}
		String gridFsId = myFile.getGridFsId();
		byte[] bytebyfsId = fileService.getBytebyfsId(gridFsId);
		return header.body(bytebyfsId);

	}

	/**
	 * 在线显示文件
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/view/{id}")
	@ResponseBody
	public ResponseEntity<Object> serveFileOnline(@PathVariable String id) throws IOException {
		Optional<File> file = fileService.getFileById(id);
		if(!file.isPresent()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
			}
		File myFile = file.get();

		ResponseEntity.BodyBuilder header = ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.get().getName() + "\"")
				.header(HttpHeaders.CONTENT_TYPE, file.get().getContentType())
				.header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close");

		if(myFile.getContent() != null){
			return header.body(myFile.getContent().getData());
		}
		String gridFsId = myFile.getGridFsId();
		byte[] bytebyfsId = fileService.getBytebyfsId(gridFsId);
		return header.body(bytebyfsId);
	}

	/**
	 * 上传
	 * 
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		if(file.getSize()==0){
			return "请核对文件信息";
		}
			File f = null;
		try {

			f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(),null);
			f.setMd5(MD5Util.getMD5(file.getInputStream()));
			if (file.getSize() > maxSize){
				fileService.saveFile(f,file);
			}else{
				f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(),
						new Binary(file.getBytes()));
				f.setMd5(MD5Util.getMD5(file.getInputStream()));
				fileService.saveFile(f);
			}
		} catch (IOException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "Your " + file.getOriginalFilename() + " is wrong!");
			return "redirect:/";
		}

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/";
	}

	/**
	 * 上传接口
	 * 
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
		File returnFile = null;
		try {
			File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(),
					new Binary(file.getBytes()));
			f.setMd5(MD5Util.getMD5(file.getInputStream()));
			returnFile = fileService.saveFile(f);
			String path = "//" + serverAddress + ":" + serverPort + "/view/" + returnFile.getId();
			return ResponseEntity.status(HttpStatus.OK).body(path);

		} catch (IOException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}

	}

	/**
	 * 删除文件
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseEntity<String> deleteFile(@PathVariable String id) {
		Optional<File> file = fileService.getFileById(id);
		if(!file.isPresent()){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件不存在!!");
		}
		File fee = file.get();
		if(null ==fee.getGridFsId() || "".equals(fee.getGridFsId().trim())){
			fileService.removeFile(id);
			return ResponseEntity.status(HttpStatus.OK).body("DELETE Success!");
		}
		fileService.removeFile(id,fee.getGridFsId());
		return ResponseEntity.status(HttpStatus.OK).body("DELETE Success!");
	}
}
