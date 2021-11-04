package com.hith.hithium.upload.service.impl;

import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.dao.DocumentOperationRecordDao;
import com.hith.hithium.upload.entity.DocumentOperationRecord;
import com.hith.hithium.upload.service.DocumentOperationRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentOperationRecordServiceImpl implements DocumentOperationRecordService {

    @Resource
    private DocumentOperationRecordDao operationRecordDao;

    @Override
    public Page<DocumentOperationRecord> selectDocumentOperationRecord(Integer page, Integer pageSize, String searchText) {
        Query query = new Query();
        query.skip(((long) page - 1) * pageSize).limit(pageSize);
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(searchText)) {
            List<Criteria> criteriaList = new ArrayList<>();
            String[] searchSplit = searchText.split(",");
            if (searchSplit.length > 0) {
                for (String search : searchSplit) {
                    criteriaList.add(Criteria.where("operationCustomerName").regex(search));
                    criteriaList.add(Criteria.where("operationCustomerEmployeeNum").regex(search));
                    criteriaList.add(Criteria.where("operationFileDocumentFileNames").regex(search));
                    criteriaList.add(Criteria.where("operationBehavior").regex(search));
                    criteriaList.add(Criteria.where("operationCustomerDepartment").regex(search));
                }
                if(!criteriaList.isEmpty()){
                    criteria.orOperator(criteriaList);
                }
            }
        }
        query.addCriteria(criteria).with(Sort.by(Sort.Order.desc("id")));
        Query countQuery = new Query().addCriteria(criteria);
        long count = operationRecordDao.getDocumentOperationRecordCount(countQuery);
        List<DocumentOperationRecord> documentOperationRecordList = operationRecordDao.selectDocumentOperationRecord(query);
        long totalPage = (int) Math.ceil((double) count / (double) pageSize);
        return new Page<>(totalPage, count, documentOperationRecordList);
    }
}
