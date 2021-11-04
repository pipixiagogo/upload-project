package com.hith.hithium.upload.controller;

import com.hith.hithium.upload.entity.FileType;
import com.hith.hithium.upload.service.FileTypeService;
import com.hith.hithium.upload.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/fileType")
public class FileTypeController {

    @Resource
    private FileTypeService fileTypeService;

    @RequiresPermissions(value = "sys:power:fileType")
    @PostMapping(value = "/saveFileType")
    public ResHelper<Void> saveFileType(@RequestParam(value = "type",required = false) String type) {
        if (StringUtils.isEmpty(type)) {
            return ResHelper.pamIll();
        }
        if(fileTypeService.sameOfFileType(type)){
            return ResHelper.error("添加文件类别失败:文件类别重复");
        }
        String saveFileTypeResult = fileTypeService.saveFileType(type);
        if (!StringUtils.isEmpty(saveFileTypeResult)) {
            return ResHelper.success("添加文件类别成功");
        }
        return ResHelper.error("添加文件类别失败");
    }

    @RequiresPermissions(value = "sys:power:fileType")
    @PostMapping(value = "/updateFileType")
    public ResHelper<Void> updateFileType(@RequestParam(value = "id",required = false) String id,
                                    @RequestParam(value = "type",required = false) String type) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(type)) {
            return ResHelper.pamIll();
        }
        if(fileTypeService.sameOfFileType(type)){
            return ResHelper.error("修改文件类别失败:文件类别重复");
        }
        if (fileTypeService.updateFileType(id, type) > 0) {
            return ResHelper.success("修改文件类别成功");
        }
        return ResHelper.error("修改文件类别失败");
    }

    @GetMapping(value = "/selectFileType")
    public ResHelper<List<FileType>> selectFileType(@RequestParam(value = "searchText")String searchText) {
        return ResHelper.success("", fileTypeService.selectFileType(searchText));
    }
    @RequiresPermissions(value = "sys:power:fileType")
    @PostMapping(value = "/removeFileType")
    public ResHelper<Void> removeFileType(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isEmpty(id)){
            return ResHelper.pamIll();
        }
        if(fileTypeService.removeFileType(id) > 0){
            return ResHelper.success("删除文件类别成功");
        }
        return ResHelper.error("删除文件类别失败");
    }

}
