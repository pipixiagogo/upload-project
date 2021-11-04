package com.hith.hithium.upload.dao;

import com.hith.hithium.upload.entity.ProjectInfo;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface ProjectInfoDao {
    String saveProjectInfo(ProjectInfo projectInfo);

    Long updateProjectInfo(Query query, Update update);

    List<ProjectInfo> selectProjectInfo(Query query);

    Long removeProjectInfo(Query query);

    boolean existProjectInfo(Query query);

    ProjectInfo selectProjectInfoById(Query query);

    boolean sameOfProjectNumAndName(Query projectNum);
}
