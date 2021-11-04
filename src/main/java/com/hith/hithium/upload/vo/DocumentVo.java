package com.hith.hithium.upload.vo;

import com.hith.hithium.upload.entity.FileType;
import com.hith.hithium.upload.entity.ProjectInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVo {
    private String id;
    private String fileName;
    /**
     * 上传人名称
     */
    private String uploadCustomerName;
    private String uploadCustomerNum;
    private String uploadCustomerId;
    private String uploadCustomerDepartment;


    private long size;

    private Date uploadDate;
    private ProjectInfo projectInfo;
    private List<FileType> fileTypeList;

    private String prefix;

}
