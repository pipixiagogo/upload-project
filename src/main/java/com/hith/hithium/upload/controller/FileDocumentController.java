package com.hith.hithium.upload.controller;

import com.hith.hithium.upload.annotation.DocumentOperationRecord;
import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.service.CustomerService;
import com.hith.hithium.upload.service.FileDocumentService;
import com.hith.hithium.upload.service.ProjectInfoService;
import com.hith.hithium.upload.utils.ResHelper;
import com.hith.hithium.upload.vo.DocumentVo;
import com.hith.hithium.upload.vo.FileDocumentVo;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.hith.hithium.upload.common.Const.SINGLE_DOWNLOAD;

@RestController
@RequestMapping(value = "/fileDocument")
public class FileDocumentController {
    private static final Logger log = LoggerFactory.getLogger(FileDocumentController.class);
    @Resource
    private FileDocumentService fileDocumentService;
    @Resource
    private ProjectInfoService projectInfoService;
    @Resource
    private CustomerService customerService;

    @GetMapping(value = "/selectFileDocument")
    public ResHelper<Page<DocumentVo>> selectFileDocument(@RequestParam(value = "searchText", required = false) String searchText,
                                                          @RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        return ResHelper.success("", fileDocumentService.selectFileDocument(searchText, page, pageSize));
    }

    @PostMapping(value = "/documentUpload")
    public ResHelper<Void> documentUpload(
            @RequestHeader(value = "ticket") String ticket,
            @RequestParam(value = "file") MultipartFile[] file,
            @RequestParam(value = "fileTypeIds", required = false) String fileTypeIds,
            @RequestParam(value = "id", required = false) String id) {
        if (!StringUtils.isEmpty(id)) {
            if (!projectInfoService.existProjectInfo(id)) {
                return ResHelper.pamIll();
            }
        }
        Customer customer = customerService.queryCustomerByTicket(ticket);
        if (customer != null && file != null && file.length > 0) {
            String fileDocumentId = fileDocumentService.documentUpload(customer, file, fileTypeIds, id);
            if (!StringUtils.isEmpty(fileDocumentId)) {
                return ResHelper.success("文件上传成功");
            }
        }
        return ResHelper.error("文件上传失败");
    }
    @DocumentOperationRecord(value = SINGLE_DOWNLOAD)
    @GetMapping(value = "/singleDownload/{id}")
    public ResponseEntity singleDownload(@PathVariable(value = "id") String id) {
        FileDocumentVo fileDocumentVo = fileDocumentService.singleDownLoad(id);
        if (fileDocumentVo != null) {
            try {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + URLEncoder.encode(fileDocumentVo.getFileName(), "utf-8"))
                        .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                        .header(HttpHeaders.CONTENT_LENGTH, fileDocumentVo.getSize() + "").header("Connection", "close")
                        .body(fileDocumentVo.getContent());
            } catch (UnsupportedEncodingException e) {
                log.error("导出文件失败:{}", e);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("文件未找到");
    }
    @DocumentOperationRecord(value = "批量下载文件")
    @GetMapping(value = "/batchDownload")
    public ResponseEntity batchDownload(@RequestParam(name = "ids", required = false) String[] ids) {
        if (ids != null && ids.length > 0) {
            FileDocumentVo fileDocumentVo = fileDocumentService.batchDownLoad(ids);
            if (fileDocumentVo != null) {
                try {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + URLEncoder.encode(fileDocumentVo.getFileName(), "utf-8"))
                            .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                            .header(HttpHeaders.CONTENT_LENGTH, fileDocumentVo.getSize() + "").header("Connection", "close")
                            .body(fileDocumentVo.getContent());
                } catch (UnsupportedEncodingException e) {
                    log.error("批量导出文件生成zip失败:{}", e);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("文件不存在");
    }
    @DocumentOperationRecord(value = "批量删除文件")
    @RequiresPermissions(value = "sys:power:fileDocument")
    @PostMapping(value = "/batchRemoveFileDocument")
    public ResHelper<Void> batchRemoveFileDocument(@RequestParam(name = "ids", required = false) String[] ids) {
        if (ids != null && ids.length > 0) {
            String delCount = fileDocumentService.batchRemoveFileDocument(ids);
            if (!StringUtils.isEmpty(delCount)) {
                return ResHelper.success("批量删除文件成功");
            }
        }
        return ResHelper.error("批量删除文件失败");
    }

    @PostMapping(value = "/updateFileDocument")
    public ResHelper<Void> updateFileDocument(@RequestHeader(value = "ticket")String ticket,
                                        @RequestParam(name = "id", required = false) String id,
                                        @RequestParam(name = "fileName", required = false) String fileName,
                                        @RequestParam(value = "fileTypeIds", required = false) String fileTypeIds,
                                        @RequestParam(value = "projectId", required = false) String projectId) {
        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(fileName)){
            return ResHelper.pamIll();
        }
        if (!StringUtils.isEmpty(projectId)) {
            if (!projectInfoService.existProjectInfo(projectId)) {
                return ResHelper.pamIll();
            }
        }
        Customer customer = customerService.queryCustomerByTicket(ticket);
        if(customer == null){
            return ResHelper.error("修改文件信息失败");
        }
        if(fileDocumentService.updateFileDocument(customer,id,fileName,fileTypeIds,projectId) > 0){
            return ResHelper.success("修改文件信息成功");
        }
        return ResHelper.error("修改文件信息失败");
    }

    @GetMapping(value = "/view/{id}")
    public ResponseEntity view(@PathVariable(value = "id") String id) {
        FileDocumentVo fileDocumentVo = fileDocumentService.viewFileDocument(id);
        if(fileDocumentVo != null){
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + fileDocumentVo.getFileName())
                    .header(HttpHeaders.CONTENT_TYPE, fileDocumentVo.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, fileDocumentVo.getSize() + "").header("Connection", "close")
                    .header(HttpHeaders.CONTENT_LENGTH , fileDocumentVo.getSize() + "")
                    .body(fileDocumentVo.getContent());
        }
        return ResponseEntity.status(HttpStatus.OK).body("文件不存在/该文件不支持预览");
    }

}
