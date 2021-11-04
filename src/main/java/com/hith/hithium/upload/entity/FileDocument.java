package com.hith.hithium.upload.entity;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class FileDocument {
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
    private String contentType;
    private String md5;
    private Date uploadDate;
    private String prefix;
    private String suffix;
    private String fileId;
    private byte[] content;
    /**
     * 文件类别 多个类别使用,分割  STRUCTURE("结构"),
     *     ELECTRICAL("电气"),
     *     COMMUNICATION("通讯"),
     *     SYSTEM("系统");
     */
    private Set<FileType> fileType;

    private ProjectInfo projectInfo;
}
