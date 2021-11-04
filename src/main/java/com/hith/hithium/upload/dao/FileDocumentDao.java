package com.hith.hithium.upload.dao;

import com.hith.hithium.upload.entity.FileDocument;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.InputStream;
import java.util.List;

public interface FileDocumentDao {
    FileDocument getDocument(Query query);

    String store(InputStream in, String fileName);

    String saveFileDocument(FileDocument fileDocument);

    FileDocument getDocumentById(String  id);

    GridFSFile getGridFSFile(Query query);

    GridFSDownloadStream getGridFSBucket(ObjectId objectId);

    Long updateFileDocumentByQuery(Query fileDocumentQuery, Update update);

    List<FileDocument> selectFileDocument(Query query);

    long selectFileDocumentCount(Query query);

    boolean selectCustomerByEmployeeNum(Query query);

    List<FileDocument> saveBatchFileDocument(List<FileDocument> fileDocumentList);

    Long removeFileDocument(Query temDelQuery,Query fsDelQuery);

}
