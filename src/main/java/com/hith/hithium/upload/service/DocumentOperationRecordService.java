package com.hith.hithium.upload.service;

import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.entity.DocumentOperationRecord;

public interface DocumentOperationRecordService {

    Page<DocumentOperationRecord> selectDocumentOperationRecord(Integer page, Integer pageSize, String searchText);
}
