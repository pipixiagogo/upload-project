package com.hith.hithium.upload.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DocumentOperationRecord {

    private String id;

    private String operationCustomerName;

    private String operationCustomerEmployeeNum;

    private String operationCustomerDepartment;

    private Date operationDate;

    private String operationFileDocumentFileNames;

    private String operationBehavior;

    private long operationTakesTime;

    private Boolean onTheJob;




}
