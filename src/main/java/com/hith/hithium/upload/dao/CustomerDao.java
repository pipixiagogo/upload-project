package com.hith.hithium.upload.dao;

import com.hith.hithium.upload.entity.Customer;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Set;

public interface CustomerDao {
    Customer queryCustomerByTicket(String accessToken);
    Set<String> queryPermissions(Customer customer);

    String saveCustomer(Customer customer);

    Long updateCustomer(Query query, Update update);

    List<Customer> selectCustomer(Query query);

    long selectCustomerCount(Query query);

    Long removeCustomer(Query query);

    Customer getCustomerByEmployeeNum(Query customerNum);
}
