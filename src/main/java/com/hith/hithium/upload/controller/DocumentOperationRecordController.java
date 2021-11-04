package com.hith.hithium.upload.controller;

import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.entity.DocumentOperationRecord;
import com.hith.hithium.upload.service.DocumentOperationRecordService;
import com.hith.hithium.upload.utils.ResHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/documentOperationRecord")
public class DocumentOperationRecordController {

    @Resource
    private DocumentOperationRecordService documentOperationRecordService;
    @RequiresPermissions(value = "sys:power:documentOperation")
    @GetMapping(value = "/selectDocumentOperationRecord")
    public ResHelper<Page<DocumentOperationRecord>> selectDocumentOperationRecord(@RequestParam(value = "page")Integer page,
                                                                                  @RequestParam(value = "pageSize")Integer pageSize,
                                                                                  @RequestParam(value = "searchText")String searchText){
        if(page == null || pageSize == null){
            return ResHelper.pamIll();
        }
        return ResHelper.success("",documentOperationRecordService.selectDocumentOperationRecord(page,pageSize,searchText));
    }

}
