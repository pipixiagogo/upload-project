package com.hith.hithium.upload.dao.impl;

import com.hith.hithium.upload.dao.ProjectInfoDao;
import com.hith.hithium.upload.entity.ProjectInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ProjectInfoDaoImpl implements ProjectInfoDao {

    @Resource
    private MongoTemplate mongoTemplate;
    @Override
    public String saveProjectInfo(ProjectInfo projectInfo) {
        ProjectInfo insertProject = mongoTemplate.insert(projectInfo);
        return insertProject.getId();
    }

    @Override
    public Long updateProjectInfo(Query query, Update update) {
        return  mongoTemplate.updateFirst(query,update,ProjectInfo.class).getModifiedCount();
    }

    @Override
    public List<ProjectInfo> selectProjectInfo(Query query) {
        return mongoTemplate.find(query,ProjectInfo.class);
    }

    @Override
    public Long removeProjectInfo(Query query) {
        return mongoTemplate.remove(query, ProjectInfo.class).getDeletedCount();
    }

    @Override
    public boolean existProjectInfo(Query query) {
        return  mongoTemplate.exists(query,ProjectInfo.class);
    }

    @Override
    public ProjectInfo selectProjectInfoById(Query query) {
        return mongoTemplate.findOne(query,ProjectInfo.class);
    }

    @Override
    public boolean sameOfProjectNumAndName(Query query) {
        return mongoTemplate.exists(query,ProjectInfo.class);
    }
}
