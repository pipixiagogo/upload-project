package com.hith.hithium.upload.service.impl;

import cn.hutool.core.io.IoUtil;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.hith.hithium.upload.common.FileDocumentType;
import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.config.PropertiesConfig;
import com.hith.hithium.upload.dao.FileDocumentDao;
import com.hith.hithium.upload.dao.FileTypeDao;
import com.hith.hithium.upload.dao.ProjectInfoDao;
import com.hith.hithium.upload.entity.*;
import com.hith.hithium.upload.service.FileDocumentService;
import com.hith.hithium.upload.vo.DocumentVo;
import com.hith.hithium.upload.vo.FileDocumentVo;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileDocumentServiceImpl implements FileDocumentService {

    private static final Logger log = LoggerFactory.getLogger(FileDocumentServiceImpl.class);
    @Resource
    private FileDocumentDao fileDocumentDao;
    @Resource
    private ProjectInfoDao projectInfoDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private FileTypeDao fileTypeDao;

    @Override
    public String documentUpload(Customer customer, MultipartFile[] file, String fileTypeIds, String id) {
        try {
            List<FileDocument> fileDocumentList = new ArrayList<>();
            for (MultipartFile multipartFile : file) {
                String md5 = getMD5(multipartFile);
                FileDocument fileDocument = getDocumentByMd5(md5);
                if (fileDocument != null) {
                    log.info("文件名:{}已上传", multipartFile.getOriginalFilename());
                    continue;
                }
                fileDocument = new FileDocument();
                fileDocument.setUploadCustomerDepartment(customer.getDepartment());
                fileDocument.setUploadCustomerId(customer.getId());
                fileDocument.setUploadDate(new Date(System.currentTimeMillis()));
                fileDocument.setUploadCustomerName(customer.getCustomerName());
                fileDocument.setUploadCustomerNum(customer.getEmployeeNum());
                Set<FileType> fileTypeList = getFileTypeList(fileTypeIds);
                if (fileTypeList != null && !fileTypeList.isEmpty()) {
                    fileDocument.setFileType(fileTypeList);
                }
                fileDocument.setMd5(md5);
                if (!StringUtils.isEmpty(id)) {
                    ProjectInfo projectInfo = projectInfoDao.selectProjectInfoById(new Query().addCriteria(Criteria.where("id").is(id)));
                    if (projectInfo != null) {
                        fileDocument.setProjectInfo(projectInfo);
                    }
                }
                fileDocument.setFileName(multipartFile.getOriginalFilename());
                fileDocument.setContentType(multipartFile.getContentType());
                if (!StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
                    String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
                    fileDocument.setSuffix(suffix);
                    fileDocument.setPrefix(multipartFile.getOriginalFilename().replaceAll(suffix, ""));
                }
                fileDocument.setSize(multipartFile.getSize());
                String fileId = uploadFileToGridFS(multipartFile.getInputStream());
                fileDocument.setFileId(fileId);
                fileDocumentList.add(fileDocument);
            }
            List<FileDocument> insertedDocumentList = fileDocumentDao.saveBatchFileDocument(fileDocumentList);
            if (insertedDocumentList != null) {
                return insertedDocumentList.size() + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("批量上传文件失败:{},第一个文件名:{},上传人:{}", e, file[0], customer.getCustomerName());
        }
        return null;
    }

    private Set<FileType> getFileTypeList(String fileTypeIds) {
        if (!StringUtils.isEmpty(fileTypeIds)) {
            String[] fileTypeSplits = fileTypeIds.split(",");
            if (fileTypeSplits.length > 0) {
                Set<String> fileSet = new HashSet<>(Arrays.asList(fileTypeSplits));
                Query query = new Query();
                query.addCriteria(Criteria.where("id").in(fileSet));
                return new HashSet<>(fileTypeDao.selectFileTypeList(query));
            }
        }
        return null;
    }

    private String uploadFileToGridFS(InputStream in) {
        String fileId = UUID.randomUUID().toString();
        return fileDocumentDao.store(in, fileId);
    }

    private FileDocument getDocumentByMd5(String md5) {
        Query query = new Query();
        query.addCriteria(Criteria.where("md5").is(md5));
        return fileDocumentDao.getDocument(query);
    }

    public String getMD5(MultipartFile file) throws Exception {
        MessageDigest mMessageDigest = MessageDigest.getInstance("MD5");
        InputStream fis = file.getInputStream();
        byte[] bytes = IoUtil.readBytes(fis);
        mMessageDigest.update(bytes, 0, bytes.length);
        fis.close();
        return new BigInteger(1, mMessageDigest.digest()).toString(16);
    }

    @Override
    public FileDocumentVo singleDownLoad(String id) {
        FileDocument fileDocument = fileDocumentDao.getDocumentById(id);
        if (fileDocument != null) {
            Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocument.getFileId()));
            GridFSFile gridFSFile = fileDocumentDao.getGridFSFile(query);
            if (gridFSFile != null) {
                GridFSDownloadStream gridFSBucketStream = fileDocumentDao.getGridFSBucket(gridFSFile.getObjectId());
                if (gridFSBucketStream.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(gridFSFile, gridFSBucketStream);
                    try {
                        fileDocument.setContent(IoUtil.readBytes(resource.getInputStream()));
                    } catch (IOException e) {
                        log.error("导出文件失败:{},文件名称:{}", e, fileDocument.getFileName());
                    }
                }
            }
            FileDocumentVo fileDocumentVo = new FileDocumentVo();
            BeanUtils.copyProperties(fileDocument, fileDocumentVo);
            return fileDocumentVo;
        }
        return null;
    }

    @Override
    public FileDocumentVo batchDownLoad(String[] ids) {
        Map<String, FileStreamInfo> fileStreamInfoMap = getMapByIds(ids);
        FileDocumentVo fileDocumentVo = null;
        if (!fileStreamInfoMap.isEmpty()) {
            fileDocumentVo = createZipFile(fileStreamInfoMap);
        }
        return fileDocumentVo;
    }

    private FileDocumentVo createZipFile(Map<String, FileStreamInfo> fileStreamInfoMap) {
        File zipFile = new File(propertiesConfig.getBatchDownLoadPath() + UUID.randomUUID() + ".zip");
        FileDocumentVo fileDocumentVo = new FileDocumentVo();
        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                log.error("生成zip文件失败:{}", e);
            }
        }
        FileOutputStream fileOutputStream;
        ZipOutputStream zipOutputStream;
        ZipEntry zipEntry;
        InputStream fis;
        try {
            fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            Set<Map.Entry<String, FileStreamInfo>> entries = fileStreamInfoMap.entrySet();
            for (Map.Entry<String, FileStreamInfo> entry : entries) {
                zipEntry = new ZipEntry(entry.getValue().getFileName());
                zipOutputStream.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = entry.getValue().getInputStream().read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
            }
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileOutputStream.close();
            fis = new BufferedInputStream(new FileInputStream(zipFile.getPath()));
            fileDocumentVo.setContent(IoUtil.readBytes(fis));
            fileDocumentVo.setFileName(zipFile.getName());
            fis.close();
            fileDocumentVo.setSize(zipFile.length());
        } catch (IOException e) {
            log.error("批量导出文件生成zip失败:{},文件名称:{}", e, zipFile.getName());
        }
        if (zipFile.delete()) {
            log.info("批量导出文件压缩包删除成功 压缩包名称为:{}", zipFile.getName());
        }
        return fileDocumentVo;
    }

    private Map<String, FileStreamInfo> getMapByIds(String[] ids) {
        Map<String, FileStreamInfo> map = null;
        for (String id : ids) {
            FileDocument fileDocument = fileDocumentDao.getDocumentById(id);
            if (fileDocument == null) {
                continue;
            }
            Query gridQuery = new Query().addCriteria(Criteria.where("_id").is(fileDocument.getFileId()));
            try {
                GridFSFile gridFSFile = fileDocumentDao.getGridFSFile(gridQuery);
                if (gridFSFile != null) {
                    GridFSDownloadStream downloadStream = fileDocumentDao.getGridFSBucket(gridFSFile.getObjectId());
                    if (downloadStream.getGridFSFile().getLength() > 0) {
                        GridFsResource resource = new GridFsResource(gridFSFile, downloadStream);
                        if (map == null) {
                            map = new TreeMap<>();
                        }
                        FileStreamInfo streamInfo = map.get(id);
                        if (streamInfo != null) {
                            streamInfo.setInputStream(resource.getInputStream());
                            streamInfo.setFileName(fileDocument.getFileName());
                            streamInfo.setSize(fileDocument.getSize());
                        } else {
                            FileStreamInfo fileStreamInfo = new FileStreamInfo();
                            map.put(id, fileStreamInfo);
                            fileStreamInfo.setFileName(fileDocument.getFileName());
                            fileStreamInfo.setSize(fileDocument.getSize());
                            fileStreamInfo.setInputStream(resource.getInputStream());
                        }
                    }
                }
            } catch (IOException e) {
                log.error("批量文件导出文件失败:{},文件名称:{}", e, fileDocument.getFileName());
            }
        }
        return map;
    }

    @Override
    public Page<DocumentVo> selectFileDocument(String searchText, Integer page, Integer pageSize) {
        Query query = new Query();
        query.skip((long) (page - 1) * pageSize).limit(pageSize);
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(searchText)) {
            List<Criteria> criteriaList = new ArrayList<>();
            String[] searchSplit = searchText.split(",");
            if (searchSplit.length > 0) {
                for (String search : searchSplit) {
                    criteriaList.add(Criteria.where("uploadCustomerName").regex(search));
                    criteriaList.add(Criteria.where("prefix").regex(search));
                    criteriaList.add(Criteria.where("projectInfo.projectName").regex(search));
                    criteriaList.add(Criteria.where("fileType.fileType").regex(search));
                }
            }
            if (!criteriaList.isEmpty()) {
                criteria.orOperator(criteriaList);
            }
        }
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.desc("id")));
        List<FileDocument> fileDocumentList = fileDocumentDao.selectFileDocument(query);
        Query countQuery = new Query().addCriteria(criteria);
        long count = fileDocumentDao.selectFileDocumentCount(countQuery);
        List<DocumentVo> documentVoList = fileDocumentList.stream().map(fileDocument -> {
            DocumentVo documentVo = new DocumentVo();
            BeanUtils.copyProperties(fileDocument, documentVo);
            Set<FileType> fileType = fileDocument.getFileType();
            if (fileType != null && fileType.size() > 0) {
                List<FileType> filterFileTypeList = fileType.stream().filter(Objects::nonNull).collect(Collectors.toList());
                documentVo.setFileTypeList(filterFileTypeList);
            }
            return documentVo;
        }).collect(Collectors.toList());
        long totalPage = (int) Math.ceil((double) count / (double) pageSize);
        return new Page<>(totalPage, count, documentVoList);
    }

    @Override
    public String batchRemoveFileDocument(String[] ids) {
        Query query = new Query().addCriteria(Criteria.where("id").in(ids));
        List<FileDocument> fileDocumentList = fileDocumentDao.selectFileDocument(query);
        if (!fileDocumentList.isEmpty()) {
            List<String> fileIds = fileDocumentList.stream().map(FileDocument::getFileId).collect(Collectors.toList());
            Query fsDelQuery = new Query().addCriteria(Criteria.where("_id").in(fileIds));
            Long delCount = fileDocumentDao.removeFileDocument(query, fsDelQuery);
            if (delCount == ids.length) {
                return delCount + "";
            }
        }
        return null;
    }

    @Override
    public Long updateFileDocument(Customer customer, String id, String fileName, String fileTypeIds, String projectId) {
        Query idQuery = new Query().addCriteria(Criteria.where("id").is(id));
        FileDocument fileDocument = fileDocumentDao.getDocument(idQuery);
        if (fileDocument == null || !customer.getId().equals(fileDocument.getUploadCustomerId())) {
            return 0L;
        }
        String newFileName = fileName + fileDocument.getSuffix();
        Update update = new Update().set("fileName", newFileName).set("prefix", fileName);
        Set<FileType> fileTypeList = getFileTypeList(fileTypeIds);
        if (fileTypeList != null && !fileTypeList.isEmpty()) {
            update.set("fileType", fileTypeList);
        } else {
            update.set("fileType", null);
        }
        if (!StringUtils.isEmpty(projectId)) {
            ProjectInfo projectInfo = projectInfoDao.selectProjectInfoById(new Query().addCriteria(Criteria.where("id").is(projectId)));
            if (projectInfo != null) {
                update.set("projectInfo", projectInfo);
            }
        } else {
            update.set("projectInfo", new ProjectInfo());
        }
        return fileDocumentDao.updateFileDocumentByQuery(idQuery, update);
    }

    @Override
    public FileDocumentVo viewFileDocument(String id) {
        FileDocument fileDocument = fileDocumentDao.getDocumentById(id);
        if (fileDocument != null) {
            Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocument.getFileId()));
            GridFSFile gridFSFile = fileDocumentDao.getGridFSFile(query);
            if (gridFSFile != null) {
                GridFSDownloadStream gridFSBucketStream = fileDocumentDao.getGridFSBucket(gridFSFile.getObjectId());
                if (gridFSBucketStream.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(gridFSFile, gridFSBucketStream);
                    try {
                        InputStream inputStream = resource.getInputStream();
                        FileDocumentType fileDocumentType = FileDocumentType.getFileType(inputStream);
                        inputStream.reset();
                        String suffix = fileDocument.getSuffix();
                        boolean covertAccordToTheInput = covertAccordToTheInput(suffix, fileDocumentType, inputStream, fileDocument);
                        if (covertAccordToTheInput) {
                            FileDocumentVo fileDocumentVo = new FileDocumentVo();
                            BeanUtils.copyProperties(fileDocument, fileDocumentVo);
                            return fileDocumentVo;
                        }
                    } catch (Exception e) {
                        log.error("在线预览转换失败:{},文件名称:{}", e, fileDocument.getFileName());
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private boolean covertAccordToTheInput(String suffix, FileDocumentType fileDocumentType, InputStream inputStream, FileDocument fileDocument) throws Exception {
        if (suffix.contains("doc") || suffix.contains("docx")) {
            wordToPdf(inputStream, fileDocument);
        } else if (fileDocument.getSuffix().contains("xlsx") || fileDocument.getSuffix().contains("xls")) {
            excelToPdf(inputStream, fileDocument);
        } else if (fileDocument.getSuffix().contains("pptx")) {
            pptToPdf(inputStream, fileDocument);
        } else if (fileDocument.getSuffix().contains("txt")) {
            fileDocument.setContent(IoUtil.readBytes(inputStream));
        } else if (fileDocumentType != null) {
            if (fileDocumentType.equals(FileDocumentType.PNG) || fileDocumentType.equals(FileDocumentType.BMP)
                    || fileDocumentType.equals(FileDocumentType.GIF) || fileDocumentType.equals(FileDocumentType.JPEG)
                    || fileDocumentType.equals(FileDocumentType.PDF)) {
                fileDocument.setContent(IoUtil.readBytes(inputStream));
            } else {
                log.error("该文件不支持在线预览,文件名:{}", fileDocument.getFileName());
                return false;
            }
        } else {
            log.error("该文件不支持在线预览:文件名{}", fileDocument.getFileName());
            return false;
        }
        return true;
    }

    private void pptToPdf(InputStream inputStream, FileDocument fileDocument) throws Exception {
        File file = null;
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            String licenseStr = propertiesConfig.getWordLicense();
            InputStream is = new ByteArrayInputStream(licenseStr.getBytes("UTF-8"));
            com.aspose.slides.License aposeLic = new com.aspose.slides.License();
            aposeLic.setLicense(is);
            file = new File(propertiesConfig.getPptToPdfPath() + System.currentTimeMillis() + ".pdf");
            Presentation pres = new Presentation(inputStream);
            fileOutputStream = new FileOutputStream(file);
            pres.save(fileOutputStream, com.aspose.slides.SaveFormat.Pdf);
            fileInputStream = new FileInputStream(file);
            fileDocument.setFileName(file.getName());
            fileDocument.setSize(file.length());
            fileDocument.setContentType("application/pdf");
            fileDocument.setContent(IoUtil.readBytes(fileInputStream));
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (file != null) {
                file.delete();
            }
        }


    }

    private void excelToPdf(InputStream inputStream, FileDocument fileDocument) throws Exception {
        String licenseStr = propertiesConfig.getWordLicense();
        ByteArrayInputStream is = new ByteArrayInputStream(licenseStr.getBytes());
        com.aspose.cells.License aposeLic = new com.aspose.cells.License();
        aposeLic.setLicense(is);
        FileOutputStream fileOS = null;
        FileInputStream stream = null;
        File file = null;
        try {
            file = new File(propertiesConfig.getExcelToPdfPath() + System.currentTimeMillis() + ".pdf");
            Workbook wb = new Workbook(inputStream);
            fileOS = new FileOutputStream(file);
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);
            int[] autoDrawSheets = {3};
            autoDraw(wb, autoDrawSheets);
            int[] showSheets = {0};
            printSheetPage(wb, showSheets);
            wb.save(fileOS, pdfSaveOptions);
            stream = new FileInputStream(file);
            fileDocument.setContent(IoUtil.readBytes(stream));
            fileDocument.setContentType("application/pdf");
            fileDocument.setSize(file.length());
            fileDocument.setFileName(file.getName());
        } finally {
            if (fileOS != null) {
                fileOS.flush();
                fileOS.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (file != null) {
                file.delete();
            }
        }
    }

    private void printSheetPage(Workbook wb, int[] showSheets) {
        for (int i = 1; i < wb.getWorksheets().getCount(); i++) {
            wb.getWorksheets().get(i).setVisible(false);
        }
        if (null == showSheets || showSheets.length == 0) {
            wb.getWorksheets().get(0).setVisible(true);
        } else {
            for (int i = 0; i < showSheets.length; i++) {
                wb.getWorksheets().get(i).setVisible(true);
            }
        }
    }

    private void autoDraw(Workbook wb, int[] autoDrawSheets) {
        if (null != autoDrawSheets && autoDrawSheets.length > 0) {
            for (int i = 0; i < autoDrawSheets.length; i++) {
                wb.getWorksheets().get(i).getHorizontalPageBreaks().clear();
                wb.getWorksheets().get(i).getVerticalPageBreaks().clear();
            }
        }
    }

    private void wordToPdf(InputStream inputStream, FileDocument fileDocument) throws Exception {
        FileOutputStream os = null;
        File file = null;
        FileInputStream stream = null;
        try {
            String licenseStr = propertiesConfig.getWordLicense();
            InputStream license = new ByteArrayInputStream(licenseStr.getBytes("UTF-8"));
            License asposeLic = new License();
            asposeLic.setLicense(license);
            file = new File(propertiesConfig.getWordToPdfPath() + System.currentTimeMillis() + ".pdf");
            os = new FileOutputStream(file);
            Document doc = new Document(inputStream);
            doc.save(os, SaveFormat.PDF);
            stream = new FileInputStream(file);
            fileDocument.setFileName(file.getName());
            fileDocument.setSize(file.length());
            fileDocument.setContentType("application/pdf");
            fileDocument.setContent(IoUtil.readBytes(stream));
        } finally {
            if (os != null) {
                os.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (file != null) {
                file.delete();
            }
        }

    }
}
