package com.hith.hithium.upload.dao.impl;

import com.hith.hithium.upload.dao.FileDocumentDao;
import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.entity.FileDocument;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@Repository
public class FileDocumentDaoImpl implements FileDocumentDao {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private GridFsTemplate gridFsTemplate;
    @Resource
    private GridFSBucket gridFSBucket;

    @Override
    public FileDocument getDocument(Query query) {
        return mongoTemplate.findOne(query, FileDocument.class);
    }

    @Override
    public String store(InputStream in, String fileName) {
        return gridFsTemplate.store(in, fileName).toString();
    }

    @Override
    public String saveFileDocument(FileDocument fileDocument) {
        FileDocument document = mongoTemplate.insert(fileDocument);
        return document.getId();
    }

    @Override
    public FileDocument getDocumentById(String id) {
        return mongoTemplate.findById(id, FileDocument.class);
    }

    @Override
    public GridFSFile getGridFSFile(Query query) {
        return gridFsTemplate.findOne(query);
    }

    @Override
    public GridFSDownloadStream getGridFSBucket(ObjectId objectId) {
        return gridFSBucket.openDownloadStream(objectId);
    }

    @Override
    public Long updateFileDocumentByQuery(Query fileDocumentQuery, Update update) {
        return mongoTemplate.updateMulti(fileDocumentQuery, update, FileDocument.class).getModifiedCount();
    }

    @Override
    public List<FileDocument> selectFileDocument(Query query) {
        return mongoTemplate.find(query, FileDocument.class);
    }

    @Override
    public long selectFileDocumentCount(Query query) {
        return mongoTemplate.count(query, FileDocument.class);
    }

    @Override
    public boolean selectCustomerByEmployeeNum(Query query) {
        return mongoTemplate.exists(query, Customer.class);
    }

    @Override
    public List<FileDocument> saveBatchFileDocument(List<FileDocument> fileDocumentList) {
        return (List<FileDocument>) mongoTemplate.insert(fileDocumentList,FileDocument.class);
    }

    @Override
    public Long removeFileDocument(Query temDelQuery, Query fsDelQuery) {
        gridFsTemplate.delete(fsDelQuery);
        return mongoTemplate.remove(temDelQuery,FileDocument.class).getDeletedCount();
    }
}
