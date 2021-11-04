package com.hith.hithium.upload.vo;

import com.hith.hithium.upload.entity.FileType;
import com.hith.hithium.upload.entity.ProjectInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDocumentVo {
    private List<FileType> fileTypeList;
    private String uploadCustomerName;
    private String uploadCustomerNum;
    private String uploadCustomerDepartment;
    private long size;
    private ProjectInfo projectInfo;
    private String fileName;
    private byte[] content;
    private String contentType;
}
