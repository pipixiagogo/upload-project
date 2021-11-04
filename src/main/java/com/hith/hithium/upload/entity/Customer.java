package com.hith.hithium.upload.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Customer {

    private String id;
    /**
     * 工号  初始密码跟工号相同
     */
    private String employeeNum;

    /**
     * 名称
     */
    private String customerName;
    /**
     * 部门
     */
    private String department;
    /**
     * 盐
     */
    private String salt;

    private Date createDate;

    private String permissions;

    private String password;
    private String ticket;

    private Date ticketExpireTime;

    private Boolean onTheJob;




}
