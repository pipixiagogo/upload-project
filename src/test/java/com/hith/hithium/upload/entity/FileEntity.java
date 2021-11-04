package com.hith.hithium.upload.entity;

import lombok.Data;

import java.util.List;

@Data
public class FileEntity {
    private Entity entity;
    private String id;

    private List<ListEntity> entityList;

}
