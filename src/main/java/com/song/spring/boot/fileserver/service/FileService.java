/**
 * 
 */
package com.song.spring.boot.fileserver.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.song.spring.boot.fileserver.domain.File;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * File 服务接口.
 * 
 * @since 1.0.0 2017年3月28日
 * @author <a href="https://TRANS">song</a>
 */
public interface FileService {
	/**
	 * 保存文件
	 * @param File
	 * @return
	 */
	File saveFile(File file);
	File saveFile(File file, MultipartFile baseFile);


	GridFsResource getFsRe(String gridFsId);

	
	/**
	 * 删除文件
	 * @param File
	 * @return
	 */
	void removeFile(String id);

	void removeFile(String id,String gsfsId);

	/**
	 * 根据id获取文件
	 * @param File
	 * @return
	 */
	Optional<File> getFileById(String id);

	/**
	 * 分页查询，按上传时间降序
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<File> listFilesByPage(int pageIndex, int pageSize);

	public byte[] getBytebyfsId(String fsid) throws IOException;
}
