package com.hith.hithium.upload.service;

import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.utils.ResHelper;
import com.hith.hithium.upload.vo.CustomerLoginVo;
import com.hith.hithium.upload.vo.CustomerVo;
import java.util.Set;

public interface CustomerService {

    Customer queryCustomerByTicket(String accessTicket);

    Set<String> queryPermissions(Customer customer);

    String saveCustomer(String employeeNum, String customerName, String department);

    Long updateCustomer(String id, String customerName, String department, String employeeNum, Boolean onTheJob);

    Page<CustomerVo> selectCustomer(String searchText, Integer page, Integer pageSize);

    Long removeCustomer(String id);

    CustomerLoginVo customerLogin(String employeeNum, String password);

    String sameOfEmployeeNum(String employeeNum);

    Long password(String password, String newPassword, Customer customer);

    Long resetPwd(String[] customerIds);

    Long logout(Customer customer);
}
