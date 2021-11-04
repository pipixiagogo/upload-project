package com.hith.hithium.upload.dao;

import com.hith.hithium.upload.entity.FileType;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface FileTypeDao {
    String saveFileType(FileType type);

    FileType selectFileType(Query query);

    Long updateFileType(Query queryById, Update update);

    List<FileType> selectFileTypeList(Query query);

    Long removeFileType(Query query);

    boolean sameOfFileType(Query query);
}
