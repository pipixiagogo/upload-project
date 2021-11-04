package com.hith.hithium.upload.dao;

import com.hith.hithium.upload.entity.DocumentOperationRecord;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface DocumentOperationRecordDao {
    String saveDocumentOperationRecord(DocumentOperationRecord documentOperationRecord);

    long getDocumentOperationRecordCount(Query countQuery);

    List<DocumentOperationRecord> selectDocumentOperationRecord(Query query);
}
