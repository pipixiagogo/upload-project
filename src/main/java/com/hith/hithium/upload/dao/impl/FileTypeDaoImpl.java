package com.hith.hithium.upload.dao.impl;

import com.hith.hithium.upload.dao.FileTypeDao;
import com.hith.hithium.upload.entity.FileType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class FileTypeDaoImpl implements FileTypeDao {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String saveFileType(FileType type) {
        FileType fileType = mongoTemplate.insert(type);
        return fileType.getId();
    }

    @Override
    public FileType selectFileType(Query query) {
        return mongoTemplate.findOne(query, FileType.class);
    }

    @Override
    public Long updateFileType(Query queryById, Update update) {
        return mongoTemplate.updateFirst(queryById, update, FileType.class).getModifiedCount();
    }

    @Override
    public List<FileType> selectFileTypeList(Query query) {
        return mongoTemplate.find(query, FileType.class);
    }

    @Override
    public Long removeFileType(Query query) {
        return mongoTemplate.remove(query, FileType.class).getDeletedCount();
    }

    @Override
    public boolean sameOfFileType(Query query) {
        return mongoTemplate.exists(query,FileType.class);
    }
}
