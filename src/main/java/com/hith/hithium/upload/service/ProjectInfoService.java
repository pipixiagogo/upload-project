package com.hith.hithium.upload.service;

import com.hith.hithium.upload.entity.ProjectInfo;

import java.util.List;

public interface ProjectInfoService {

    String saveProjectInfo(String projectNum, String projectName);

    Long updateProjectInfo(String id, String projectName, String projectNum);

    List<ProjectInfo> selectProjectInfo(String searchText);

    Long removeProjectInfo(String id);

    boolean existProjectInfo(String id);



    boolean sameOfProjectNumAndName(String projectNum);

    boolean sameOfProjectNumAndName(String projectNum, String id);
}
