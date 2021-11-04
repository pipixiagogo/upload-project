package com.hith.hithium.upload.entity;

import lombok.Data;

import java.io.InputStream;

@Data
public class FileStreamInfo {
    private InputStream inputStream;

    private String fileName;

    private long size;

}
