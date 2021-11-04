package com.hith.hithium.upload.dao.impl;

import cn.hutool.db.Db;
import com.hith.hithium.upload.dao.CustomerDao;
import com.hith.hithium.upload.entity.Customer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CustomerDaoImpl implements CustomerDao {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Customer queryCustomerByTicket(String accessTicket) {
        Query query = new Query();
        query.addCriteria(Criteria.where("ticket").is(accessTicket));
        return mongoTemplate.findOne(query, Customer.class);
    }

    @Override
    public Set<String> queryPermissions(Customer customer) {
        Set<String> permsSet = new HashSet<>();
        if (!StringUtils.isEmpty(customer.getPermissions())) {
            permsSet.addAll(Arrays.asList(customer.getPermissions().split(",")));
        }
        return permsSet;
    }

    @Override
    public String saveCustomer(Customer customer) {
        Customer customerResult = mongoTemplate.insert(customer);
        return customerResult.getId();
    }

    @Override
    public Long updateCustomer(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, Customer.class).getModifiedCount();
    }

    @Override
    public List<Customer> selectCustomer(Query query) {
        return mongoTemplate.find(query,Customer.class);
    }

    @Override
    public long selectCustomerCount(Query query) {
        return mongoTemplate.count(query,Customer.class);
    }

    @Override
    public Long removeCustomer(Query query) {
        return  mongoTemplate.remove(query,Customer.class).getDeletedCount();
    }

    @Override
    public Customer getCustomerByEmployeeNum(Query query) {
        return  mongoTemplate.findOne(query, Customer.class);
    }
}
