package com.hith.hithium.upload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInfo {
    private String id;

    private String projectNum;

    private String projectName;

    private Date projectCreateDate;
}
