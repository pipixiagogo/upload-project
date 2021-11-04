package com.hith.hithium.upload.service.impl;

import com.hith.hithium.upload.dao.FileDocumentDao;
import com.hith.hithium.upload.dao.FileTypeDao;
import com.hith.hithium.upload.entity.FileType;
import com.hith.hithium.upload.service.FileTypeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FileTypeServiceImpl implements FileTypeService {
    private static final Logger log = LoggerFactory.getLogger(FileTypeServiceImpl.class);
    @Resource
    private FileTypeDao fileTypeDao;
    @Resource
    private FileDocumentDao fileDocumentDao;

    @Override
    public String saveFileType(String type) {

        FileType fileType = new FileType();
        fileType.setFileType(type);
        return fileTypeDao.saveFileType(fileType);
    }

    @Override
    public Long updateFileType(String id, String type) {
        Query fileDocumentQuery = new Query();
        fileDocumentQuery.addCriteria(Criteria.where("fileType.id").is(id));
        Update update = new Update().set("fileType.$.fileType", type);
        Long updateFileDocumentByQuery = fileDocumentDao.updateFileDocumentByQuery(fileDocumentQuery, update);
        log.info("修改--->批量修改文档类别信息成功,修改文档数量为:{}", updateFileDocumentByQuery);
        Update updateFileType = new Update();
        updateFileType.set("fileType", type);
        Query queryById = new Query();
        queryById.addCriteria(Criteria.where("id").is(id));
        return fileTypeDao.updateFileType(queryById, updateFileType);
    }

    @Override
    public List<FileType> selectFileType(String searchText) {
        Query query = new Query();
        if(!StringUtils.isEmpty(searchText)){
            query.addCriteria(Criteria.where("fileType").regex(searchText));
        }
        query.with(Sort.by(Sort.Order.desc("id")));
        return fileTypeDao.selectFileTypeList(query);
    }

    @Override
    public Long removeFileType(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Query fileDocumentQuery = new Query();
        fileDocumentQuery.addCriteria(Criteria.where("fileType.id").is(id));
        Update update = new Update().unset("fileType.$");
        Long updateFileDocumentByQuery = fileDocumentDao.updateFileDocumentByQuery(fileDocumentQuery, update);
        log.info("删除--->批量删除文档类别信息成功,删除文档类别数量为:{}", updateFileDocumentByQuery);
        return fileTypeDao.removeFileType(query);
    }

    @Override
    public boolean sameOfFileType(String type) {
        Query query = new Query().addCriteria(Criteria.where("fileType").is(type));
        return  fileTypeDao.sameOfFileType(query);
    }
}
