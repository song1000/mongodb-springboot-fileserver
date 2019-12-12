package com.song.spring.boot.fileserver.repository;

import com.song.spring.boot.fileserver.domain.File;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * File 存储库.
 * 
 * @since 1.0.0 2017年3月28日
 * @author <a href="https://TRANS">song</a>
 */
public interface FileRepository extends MongoRepository<File, String> {
}
