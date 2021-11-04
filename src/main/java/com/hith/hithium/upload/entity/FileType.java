package com.hith.hithium.upload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileType {
    private String id;
    /**
     * 类别
     */
    private String fileType;
}
