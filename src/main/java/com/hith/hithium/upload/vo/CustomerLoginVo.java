package com.hith.hithium.upload.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLoginVo {

    private String customerName;

    private String ticket;

    private String department;

    private String employeeNum;

    private long uploadFileSize;
}
