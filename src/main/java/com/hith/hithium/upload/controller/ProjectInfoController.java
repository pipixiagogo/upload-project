package com.hith.hithium.upload.controller;

import com.hith.hithium.upload.entity.ProjectInfo;
import com.hith.hithium.upload.service.ProjectInfoService;
import com.hith.hithium.upload.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/projectInfo")
public class ProjectInfoController {
    @Resource
    private ProjectInfoService projectInfoService;
    @RequiresPermissions(value = "sys:power:projectInfo")
    @PostMapping(value = "/saveProjectInfo")
    public ResHelper<Void> saveProjectInfo(@RequestParam(value = "projectNum", required = false) String projectNum,
                                     @RequestParam(value = "projectName", required = false) String projectName) {
        if (StringUtils.isEmpty(projectName) || StringUtils.isEmpty(projectNum)) {
            return ResHelper.error("项目编号/名称不能为空");
        }
        if(projectInfoService.sameOfProjectNumAndName(projectNum)){
            return ResHelper.error("添加项目信息失败:项目编号重复");
        }
        String saveResult = projectInfoService.saveProjectInfo(projectNum, projectName);
        if (StringUtils.isEmpty(saveResult)) {
            return ResHelper.error("添加项目信息失败");
        }
        return ResHelper.success("添加项目信息成功");
    }
    @RequiresPermissions(value = "sys:power:projectInfo")
    @PostMapping(value = "/updateProjectInfo")
    public ResHelper<Void> updateProjectInfo(@RequestParam(value = "id", required = false) String id,
                                       @RequestParam(value = "projectNum", required = false) String projectNum,
                                       @RequestParam(value = "projectName", required = false) String projectName) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(projectNum) || StringUtils.isEmpty(projectName)) {
            return ResHelper.error("项目编码/名称不能为空");
        }
        if(projectInfoService.sameOfProjectNumAndName(projectNum,id)){
            return ResHelper.error("修改项目信息失败:项目编号重复");
        }
        if (projectInfoService.updateProjectInfo(id, projectName, projectNum) > 0) {
            return ResHelper.success("修改项目信息成功");
        }
        return ResHelper.error("修改项目信息失败");
    }

    @GetMapping(value = "/selectProjectInfo")
    public ResHelper<List<ProjectInfo>> selectProjectInfo(@RequestParam(value = "searchText", required = false) String searchText) {
        return ResHelper.success("", projectInfoService.selectProjectInfo(searchText));
    }
    @RequiresPermissions(value = "sys:power:projectInfo")
    @PostMapping(value = "/removeProjectInfo")
    public ResHelper<Void> removeProjectInfo(@RequestParam(value = "id", required = false) String id) {
        if (StringUtils.isEmpty(id)) {
            return ResHelper.pamIll();
        }
        if (projectInfoService.removeProjectInfo(id) > 0) {
            return ResHelper.success("删除项目信息成功");
        }
        return ResHelper.error("删除项目信息失败");
    }
}
