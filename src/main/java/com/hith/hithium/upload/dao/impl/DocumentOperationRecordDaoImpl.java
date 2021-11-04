package com.hith.hithium.upload.dao.impl;

import com.hith.hithium.upload.dao.DocumentOperationRecordDao;
import com.hith.hithium.upload.entity.DocumentOperationRecord;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class DocumentOperationRecordDaoImpl implements DocumentOperationRecordDao {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String saveDocumentOperationRecord(DocumentOperationRecord documentOperationRecord) {
        DocumentOperationRecord operationRecord = mongoTemplate.insert(documentOperationRecord);
        return operationRecord.getId();
    }

    @Override
    public long getDocumentOperationRecordCount(Query countQuery) {
        return mongoTemplate.count(countQuery, DocumentOperationRecord.class);
    }

    @Override
    public List<DocumentOperationRecord> selectDocumentOperationRecord(Query query) {
        return mongoTemplate.find(query,DocumentOperationRecord.class);
    }
}
