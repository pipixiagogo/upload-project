package com.hith.hithium.upload.service.impl;

import com.hith.hithium.upload.dao.FileDocumentDao;
import com.hith.hithium.upload.dao.ProjectInfoDao;
import com.hith.hithium.upload.entity.ProjectInfo;
import com.hith.hithium.upload.service.ProjectInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    private static final Logger log = LoggerFactory.getLogger(ProjectInfoServiceImpl.class);
    @Resource
    private ProjectInfoDao projectInfoDao;
    @Resource
    private FileDocumentDao fileDocumentDao;

    @Override
    public String saveProjectInfo(String projectNum, String projectName) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectNum(projectNum);
        projectInfo.setProjectName(projectName);
        projectInfo.setProjectCreateDate(new Date(System.currentTimeMillis()));
        return projectInfoDao.saveProjectInfo(projectInfo);
    }

    @Override
    public Long updateProjectInfo(String id, String projectName, String projectNum) {
        Query queryNum = new Query().addCriteria(Criteria.where("projectNum").is(projectNum));
        ProjectInfo projectInfo = projectInfoDao.selectProjectInfoById(queryNum);
        if (projectInfo != null && !projectInfo.getId().equals(id)) {
            return 0L;
        }
        Query queryExited = new Query().addCriteria(Criteria.where("projectNum").is(projectNum).and("id").is(id).and("projectName").is(projectName));
        if (projectInfoDao.selectProjectInfoById(queryExited) != null) {
            return 1L;
        }
        Query batchQuery = new Query();
        batchQuery.addCriteria(Criteria.where("projectInfo.id").is(id));
        Update batchUpdate = new Update().set("projectInfo.projectNum", projectNum).set("projectInfo.projectName", projectName);
        Long updateFileDocumentByQuery = fileDocumentDao.updateFileDocumentByQuery(batchQuery, batchUpdate);
        log.info("修改--->批量修改文档信息成功,修改文档数量为:{}", updateFileDocumentByQuery);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Update update = new Update();
        update.set("projectNum", projectNum).set("projectName", projectName);
        return projectInfoDao.updateProjectInfo(query, update);
    }

    @Override
    public List<ProjectInfo> selectProjectInfo(String searchText) {
        Query query = new Query();
        if (!StringUtils.isEmpty(searchText)) {
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("projectNum").regex(".*?" + searchText + ".*", "i"),
                    Criteria.where("projectName").regex(".*?" + searchText + ".*", "i"));
            query.addCriteria(criteria);
        }
        query.with(Sort.by(Sort.Order.desc("id")));
        return projectInfoDao.selectProjectInfo(query);
    }

    @Override
    public Long removeProjectInfo(String id) {
        Query batchQuery = new Query();
        batchQuery.addCriteria(Criteria.where("projectInfo.id").is(id));
        ProjectInfo projectInfo = new ProjectInfo();
        Update batchUpdate = new Update().set("projectInfo", projectInfo);
        Long updateFileDocumentByQuery = fileDocumentDao.updateFileDocumentByQuery(batchQuery, batchUpdate);
        log.info("删除--->批量修改文档信息成功,修改文档数量为:{}", updateFileDocumentByQuery);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return projectInfoDao.removeProjectInfo(query);
    }

    @Override
    public boolean existProjectInfo(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return projectInfoDao.existProjectInfo(query);
    }

    @Override
    public boolean sameOfProjectNumAndName(String projectNum) {
        return projectInfoDao.sameOfProjectNumAndName(new Query().addCriteria(Criteria.where("projectNum").is(projectNum)));
    }

    @Override
    public boolean sameOfProjectNumAndName(String projectNum, String id) {
        ProjectInfo projectInfo = projectInfoDao.selectProjectInfoById(new Query().addCriteria(Criteria.where("projectNum").is(projectNum)));
        return projectInfo != null && !projectInfo.getId().equals(id);
    }
}
