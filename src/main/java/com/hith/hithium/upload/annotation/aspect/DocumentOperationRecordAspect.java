package com.hith.hithium.upload.annotation.aspect;

import com.hith.hithium.upload.annotation.DocumentOperationRecord;
import com.hith.hithium.upload.dao.DocumentOperationRecordDao;
import com.hith.hithium.upload.dao.FileDocumentDao;
import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.entity.FileDocument;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;

import static com.hith.hithium.upload.common.Const.SINGLE_DOWNLOAD;

/**
 * TODO  查询操作日志接口
 */
@Aspect
@Component
public class DocumentOperationRecordAspect {
    private static final Logger logger = LoggerFactory.getLogger(DocumentOperationRecordAspect.class);
    @Resource
    private FileDocumentDao fileDocumentDao;
    @Resource
    private DocumentOperationRecordDao documentOperationRecordDao;

    @Pointcut("@annotation(com.hith.hithium.upload.annotation.DocumentOperationRecord)")
    public void documentOperationPointCut() {
    }

    @Around(value = "documentOperationPointCut()")
    public Object documentOperationPointCutAround(ProceedingJoinPoint point) throws Throwable {
        long methodStartTime = System.currentTimeMillis();
        com.hith.hithium.upload.entity.DocumentOperationRecord documentOperationRecord = getDocumentOperationRecord(point);
        Object proceed = point.proceed();
        long methodEndTime = System.currentTimeMillis();
        documentOperationRecord.setOperationTakesTime((methodEndTime - methodStartTime));
        if (StringUtils.isEmpty(saveDocumentOperationRecord(documentOperationRecord))) {
            logger.info("记录操作行为:{}日志失败,操作人:{}", documentOperationRecord.getOperationBehavior(), documentOperationRecord.getOperationCustomerName());
        } else {
            logger.info("记录操作行为:{}日志成功,操作人:{}", documentOperationRecord.getOperationBehavior(), documentOperationRecord.getOperationCustomerName());
        }
        return proceed;
    }

    private String saveDocumentOperationRecord(com.hith.hithium.upload.entity.DocumentOperationRecord documentOperationRecord) {
        return documentOperationRecordDao.saveDocumentOperationRecord(documentOperationRecord);
    }

    private com.hith.hithium.upload.entity.DocumentOperationRecord getDocumentOperationRecord(ProceedingJoinPoint point) {
        com.hith.hithium.upload.entity.DocumentOperationRecord documentOperationRecord = new com.hith.hithium.upload.entity.DocumentOperationRecord();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DocumentOperationRecord annotation = method.getAnnotation(DocumentOperationRecord.class);
        String singleDownLoadArg = "";
        if (!StringUtils.isEmpty(annotation.value())) {
            documentOperationRecord.setOperationBehavior(annotation.value());
            if (annotation.value().equals(SINGLE_DOWNLOAD)) {
                singleDownLoadArg = (String) point.getArgs()[0];
            }
        }
        if (!StringUtils.isEmpty(singleDownLoadArg)) {
            Query query = new Query().addCriteria(Criteria.where("id").is(singleDownLoadArg));
            //根据文档ID查询文档名称
            FileDocument fileDocumentDaoDocument = fileDocumentDao.getDocument(query);
            if (fileDocumentDaoDocument != null) {
                documentOperationRecord.setOperationFileDocumentFileNames(fileDocumentDaoDocument.getFileName());
            } else {
                documentOperationRecord.setOperationFileDocumentFileNames(SINGLE_DOWNLOAD + "不存在");
            }
        } else {
            String[] arg = (String[]) point.getArgs()[0];
            for (int i = 0; i < arg.length; i++) {
                Query query = new Query().addCriteria(Criteria.where("id").is(arg[i]));
                //根据文档ID查询文档名称
                FileDocument fileDocumentDaoDocument = fileDocumentDao.getDocument(query);
                StringBuilder stringBuilder = null;
                if (fileDocumentDaoDocument != null && !StringUtils.isEmpty(documentOperationRecord.getOperationFileDocumentFileNames())) {
                    stringBuilder = new StringBuilder(documentOperationRecord.getOperationFileDocumentFileNames());
                } else {
                    stringBuilder = new StringBuilder();
                }
                if (fileDocumentDaoDocument != null) {
                    if (i == arg.length - 1) {
                        stringBuilder.append(fileDocumentDaoDocument.getFileName());
                    } else {
                        stringBuilder.append(fileDocumentDaoDocument.getFileName()).append(",");
                    }
                    documentOperationRecord.setOperationFileDocumentFileNames(stringBuilder.toString());
                } else {
                    documentOperationRecord.setOperationFileDocumentFileNames(annotation.value() + "不存在");
                }
            }
        }
        Customer customer = (Customer) SecurityUtils.getSubject().getPrincipal();
        documentOperationRecord.setOperationCustomerDepartment(customer.getDepartment());
        documentOperationRecord.setOperationCustomerName(customer.getCustomerName());
        documentOperationRecord.setOperationCustomerEmployeeNum(customer.getEmployeeNum());
        documentOperationRecord.setOperationDate(new Date(System.currentTimeMillis()));
        documentOperationRecord.setOnTheJob(customer.getOnTheJob());
        return documentOperationRecord;
    }
}
