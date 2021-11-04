package com.hith.hithium.upload.vo;


import lombok.Data;

@Data
public class CustomerVo {

    private String id;
    /**
     * 名称
     */
    private String customerName;
    /**
     * 部门
     */
    private String department;

    private String employeeNum;

    private Boolean onTheJob;
}
