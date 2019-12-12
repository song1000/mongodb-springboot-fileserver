package com.song.spring.boot.fileserver.service;

import java.io.IOException;
import java.util.*;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.song.spring.boot.fileserver.util.Byte2InputStream;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.song.spring.boot.fileserver.domain.File;
import com.song.spring.boot.fileserver.repository.FileRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * File 服务.
 * 
 * @since 1.0.0 2017年7月30日
 * @author <a href="https://TRANS">song</a>
 */
@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	public FileRepository fileRepository;

	private Map<String,GridFsTemplate> gridFsMap = new HashMap<>();

	@Resource
	private MongoDbFactory mongoDbFactory;
	@Resource
	private MongoConverter converter;

	@Override
	public File saveFile(File file) {
		return fileRepository.save(file);
	}

	@Override
	public File saveFile(File file, MultipartFile baseFile) {
		try {

			 GridFsTemplate gridFsTemplate = getGridFsTemplate("trans");
			ObjectId store = gridFsTemplate.store(baseFile.getInputStream(), baseFile.getName());
			file.setGridFsId(store.toString());
		return 	this.saveFile(file);
		}catch (Exception ex){
			System.err.println("异常了 -----");
		}
		return null;
	}

	/**
	 * 根据gridFSid 获取资源块
	 * @param gridFsId
	 * @return
	 */
	@Override
	public GridFsResource getFsRe(String gridFsId) {
		GridFsTemplate gridFsTemplate = getGridFsTemplate("trans");
		GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(gridFsId)));
		GridFsResource gridFsResource = new GridFsResource(gridFSFile);
		return gridFsResource;
	}

	private GridFsTemplate getGridFsTemplate(String bucket){
		GridFsTemplate gridFsTemplate = gridFsMap.get(bucket);
		if(gridFsTemplate == null){
			GridFsTemplate song = new GridFsTemplate(mongoDbFactory, converter, bucket);
			gridFsMap.put(bucket,song);
			return song;
		}
		return gridFsMap.get(bucket);
	}

	@Override
	public byte[] getBytebyfsId(String fsid) throws IOException {
		GridFsTemplate trans = getGridFsTemplate("trans");
		GridFSFile gridFSFile = trans.findOne(Query.query(Criteria.where("_id").is(fsid)));
		// todo
		GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDbFactory.getDb(),"trans");

		GridFSDownloadStream downloadStream =
				gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
		GridFsResource gridFsResource=new GridFsResource(gridFSFile,downloadStream);
		return Byte2InputStream.inputStream2byte(gridFsResource.getInputStream());
	}

	@Override
	public void removeFile(String id) {
		fileRepository.deleteById(id);
	}

	@Override
	public void removeFile(String id, String gsfsId) {
		GridFsTemplate trans = getGridFsTemplate("trans");
		trans.delete(Query.query(Criteria.where("_id").is(gsfsId)));
		removeFile(id);
	}

	@Override
	public Optional<File> getFileById(String id) {
		return fileRepository.findById(id);
	}

	@Override
	public List<File> listFilesByPage(int pageIndex, int pageSize) {
		Page<File> page = null;
		List<File> list = null;
		
		Sort sort = new Sort(Direction.DESC,"uploadDate"); 
		Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
		
		page = fileRepository.findAll(pageable);
		list = page.getContent();
		return list;
	}
}
