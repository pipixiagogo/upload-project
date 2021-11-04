package com.hith.hithium.upload.service;

import com.hith.hithium.upload.entity.FileType;

import java.util.List;

public interface FileTypeService {
    String saveFileType(String type);

    Long updateFileType(String id, String type);

    List<FileType> selectFileType(String searchText);

    Long removeFileType(String id);

    boolean sameOfFileType(String type);
}
