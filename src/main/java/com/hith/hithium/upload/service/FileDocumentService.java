package com.hith.hithium.upload.service;

import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.vo.DocumentVo;
import com.hith.hithium.upload.vo.FileDocumentVo;
import org.springframework.web.multipart.MultipartFile;

public interface FileDocumentService {
    String documentUpload(Customer customer, MultipartFile[] file, String fileTypeIds, String id);

    FileDocumentVo singleDownLoad(String id);

    FileDocumentVo batchDownLoad(String[] ids);

    Page<DocumentVo> selectFileDocument(String searchText, Integer page, Integer pageSize);

    String batchRemoveFileDocument(String[] ids);

    Long updateFileDocument(Customer customer, String id, String fileName, String fileTypeIds, String projectId);


    FileDocumentVo viewFileDocument(String id);
}
