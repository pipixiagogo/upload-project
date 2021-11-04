package com.hith.hithium.upload.service.impl;

import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.config.PropertiesConfig;
import com.hith.hithium.upload.dao.CustomerDao;
import com.hith.hithium.upload.dao.FileDocumentDao;
import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.oauth2.TokenGenerator;
import com.hith.hithium.upload.service.CustomerService;
import com.hith.hithium.upload.vo.CustomerLoginVo;
import com.hith.hithium.upload.vo.CustomerVo;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Value("${login.upload.file.size.limit}")
    private long uploadFileSize;
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    @Resource
    private CustomerDao customerDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private FileDocumentDao fileDocumentDao;

    @Override
    public Customer queryCustomerByTicket(String accessTicket) {
        String[] ticketSplit = accessTicket.split("_");
        if (ticketSplit.length != 2) {
            return null;
        }
        return customerDao.queryCustomerByTicket(accessTicket.split("_")[1]);
    }

    @Override
    public String sameOfEmployeeNum(String employeeNum) {
        Query query = new Query();
        query.addCriteria(Criteria.where("employeeNum").is(employeeNum));
        Customer customerByEmployeeNum = customerDao.getCustomerByEmployeeNum(query);
        if (customerByEmployeeNum != null) {
            return customerByEmployeeNum.getId();
        }
        return null;
    }

    @Override
    public Set<String> queryPermissions(Customer customer) {
        return customerDao.queryPermissions(customer);
    }

    @Override
    public String saveCustomer(String employeeNum, String customerName, String department) {
        Customer customer = new Customer();
        customer.setCustomerName(customerName);
        customer.setEmployeeNum(employeeNum);
        customer.setDepartment(department);
        customer.setCreateDate(new Date(System.currentTimeMillis()));
        customer.setOnTheJob(true);
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        customer.setPassword(new Sha256Hash(employeeNum, salt).toHex());
        customer.setSalt(salt);
        return customerDao.saveCustomer(customer);
    }

    @Override
    public Long updateCustomer(String id, String customerName, String department, String employeeNum, Boolean onTheJob) {
        Query fileDocumentQuery = new Query();
        fileDocumentQuery.addCriteria(Criteria.where("uploadCustomerId").is(id));
        Update updateDocument = new Update().set("uploadCustomerName", customerName).set("uploadCustomerNum", employeeNum)
                .set("uploadCustomerDepartment", department);
        Long updateFileDocumentByQuery = fileDocumentDao.updateFileDocumentByQuery(fileDocumentQuery, updateDocument);
        log.info("修改--->批量修改文档信息成功,修改文档数量为:{}", updateFileDocumentByQuery);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Update update = new Update();
        update.set("customerName", customerName).set("department", department)
                .set("employeeNum", employeeNum).set("onTheJob", onTheJob);
        return customerDao.updateCustomer(query, update);
    }

    @Override
    public Page<CustomerVo> selectCustomer(String searchText, Integer page, Integer pageSize) {
        Query query = new Query();
        query.skip(((long)page - 1) * pageSize).limit(pageSize);
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(searchText)) {
            criteria.orOperator(Criteria.where("customerName").regex(searchText), Criteria.where("department").regex(searchText));
        }
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.desc("id")));
        List<Customer> customerList = customerDao.selectCustomer(query);
        long count = customerDao.selectCustomerCount(new Query().addCriteria(criteria));
        List<CustomerVo> customerVos = customerList.stream().map(customer -> {
            CustomerVo customerVo = new CustomerVo();
            BeanUtils.copyProperties(customer, customerVo);
            return customerVo;
        }).collect(Collectors.toList());
        long totalPage = (int)Math.ceil( (double)count / (double)pageSize );
        return new Page<>(totalPage, count, customerVos);
    }

    @Override
    public Long removeCustomer(String id) {
        Query fileDocumentQuery = new Query();
        fileDocumentQuery.addCriteria(Criteria.where("uploadCustomerId").is(id));
        Update updateDocument = new Update().set("uploadCustomerName", null).set("uploadCustomerNum", null)
                .set("uploadCustomerDepartment", null);
        Long updateFileDocumentByQuery = fileDocumentDao.updateFileDocumentByQuery(fileDocumentQuery, updateDocument);
        log.info("删除--->批量修改文档信息成功,修改文档数量为:{}", updateFileDocumentByQuery);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return customerDao.removeCustomer(query);
    }

    @Override
    public CustomerLoginVo customerLogin(String employeeNum, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("employeeNum").is(employeeNum));
        Customer customer = customerDao.getCustomerByEmployeeNum(query);
        if (customer == null || !customer.getPassword().equals(new Sha256Hash(password, customer.getSalt()).toHex())) {
            return null;
        }
        String ticket = TokenGenerator.generateValue();
        Date ticketExpireTime = new Date(System.currentTimeMillis() + propertiesConfig.getTicketExpireTime());
        customer.setTicket(ticket);
        customer.setTicketExpireTime(ticketExpireTime);
        Update update = new Update();
        update.set("ticket", ticket).set("ticketExpireTime", ticketExpireTime);
        Long updateCustomer = customerDao.updateCustomer(query, update);
        if (updateCustomer > 0) {
            CustomerLoginVo customerLoginVo = new CustomerLoginVo();
            BeanUtils.copyProperties(customer, customerLoginVo);
            customerLoginVo.setTicket(customer.getId() + "_" + ticket);
            customerLoginVo.setUploadFileSize(uploadFileSize);
            return customerLoginVo;
        }
        return null;
    }

    @Override
    public Long password(String password, String newPassword, Customer customer) {
        if (customer.getPassword().equals(new Sha256Hash(password, customer.getSalt()).toHex())) {
            String salt = RandomStringUtils.randomAlphanumeric(20);
            Date expireTime = new Date(System.currentTimeMillis() + propertiesConfig.getTicketExpireTime());
            Query query = new Query().addCriteria(Criteria.where("id").is(customer.getId()));
            String newSaltOfPassword = new Sha256Hash(newPassword, salt).toHex();
            Update update = new Update().set("password", newSaltOfPassword)
                    .set("salt", salt).set("ticket", TokenGenerator.generateValue())
                    .set("ticketExpireTime", expireTime);
            return customerDao.updateCustomer(query, update);
        }
        return 0L;
    }

    @Override
    public Long resetPwd(String[] customerIds) {
        Query query = new Query().addCriteria(Criteria.where("id").in(customerIds));
        List<Customer> customerList = customerDao.selectCustomer(query);
        long modifyCount = 0L;
        for (Customer customer : customerList) {
            Query customerQuery = new Query().addCriteria(Criteria.where("id").is(customer.getId()));
            Update update = new Update();
            String salt = RandomStringUtils.randomAlphanumeric(20);
            String newPassword = new Sha256Hash(customer.getEmployeeNum(), salt).toHex();
            Date expireTime = new Date(System.currentTimeMillis() + propertiesConfig.getTicketExpireTime());
            update.set("password", newPassword).set("salt", salt)
                    .set("ticket", TokenGenerator.generateValue())
                    .set("ticketExpireTime", expireTime);
            modifyCount = modifyCount + customerDao.updateCustomer(customerQuery, update);
        }
        return modifyCount == customerList.size() ? modifyCount : 0;
    }

    @Override
    public Long logout(Customer customer) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(customer.getId()));
        Update update = new Update();
        update.set("ticket",TokenGenerator.generateValue());
       return customerDao.updateCustomer(query,update);
    }
}
