package com.hith.hithium.upload.controller;

import com.hith.hithium.upload.common.Page;
import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.service.CustomerService;
import com.hith.hithium.upload.utils.ResHelper;
import com.hith.hithium.upload.vo.CustomerVo;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping(value = "/customer")
@RestController
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @RequiresPermissions(value = "sys:power:customer")
    @PostMapping(value = "/saveCustomer")
    public ResHelper<Void> saveCustomer(@RequestParam(value = "employeeNum", required = false) String employeeNum,
                                        @RequestParam(value = "customerName", required = false) String customerName,
                                        @RequestParam(value = "department", required = false) String department) {
        if (StringUtils.isEmpty(employeeNum) || StringUtils.isEmpty(customerName) || StringUtils.isEmpty(department)) {
            return ResHelper.error("工号/部门/姓名不能为空");
        }
        if (!StringUtils.isEmpty(customerService.sameOfEmployeeNum(employeeNum))) {
            return ResHelper.error("添加用户信息失败:存在相同工号");
        }
        String saveResult = customerService.saveCustomer(employeeNum, customerName, department);
        if (StringUtils.isEmpty(saveResult)) {
            return ResHelper.error("添加用户信息失败");
        }
        return ResHelper.success("添加用户信息成功");
    }

    @RequiresPermissions(value = "sys:power:customer")
    @PostMapping(value = "/updateCustomer")
    public ResHelper<Void> updateCustomer(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "employeeNum", required = false) String employeeNum,
            @RequestParam(value = "onTheJob", required = false) Boolean onTheJob) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(customerName) || StringUtils.isEmpty(department)
                || StringUtils.isEmpty(employeeNum) || onTheJob == null) {
            return ResHelper.pamIll();
        }
        String customerId = customerService.sameOfEmployeeNum(employeeNum);
        if (!StringUtils.isEmpty(customerId) && !id.equals(customerId)) {
            return ResHelper.error("修改用户信息失败:存在相同工号");
        }
        if (customerService.updateCustomer(id, customerName, department, employeeNum, onTheJob) > 0) {
            return ResHelper.success("修改成功");
        }
        return ResHelper.error("修改失败");
    }

    @RequiresPermissions(value = "sys:power:customer")
    @GetMapping(value = "/selectCustomer")
    public ResHelper<Page<CustomerVo>> selectCustomer(@RequestParam(value = "searchText", required = false) String searchText,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        return ResHelper.success("", customerService.selectCustomer(searchText, page, pageSize));
    }

    @RequiresPermissions(value = "sys:power:customer")
    @PostMapping(value = "/removeCustomer")
    public ResHelper<Void> removeCustomer(@RequestParam(value = "id", required = false) String id) {
        if (StringUtils.isEmpty(id)) {
            return ResHelper.pamIll();
        }
        if (customerService.removeCustomer(id) > 0) {
            return ResHelper.success("删除用户成功");
        }
        return ResHelper.error("删除用户失败");
    }

    @PostMapping(value = "/password")
    public ResHelper<Void> password(@RequestHeader(value = "ticket") String ticket,
                                    @RequestParam(value = "password", required = false) String password,
                                    @RequestParam(value = "newPassword", required = false) String newPassword) {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(newPassword)) {
            return ResHelper.pamIll();
        }
        Customer customer = customerService.queryCustomerByTicket(ticket);
        if (customer != null) {
            if (customerService.password(password, newPassword, customer) > 0) {
                return ResHelper.success("修改密码成功");
            }
        }
        return ResHelper.error("修改密码失败");
    }

    @RequiresPermissions(value = "sys:power:customer")
    @PostMapping(value = "/resetPwd")
    public ResHelper<Void> resetPwd(@RequestParam(value = "customerIds") String[] customerIds) {
        if (customerIds != null && customerIds.length > 0) {
            if (customerService.resetPwd(customerIds) > 0) {
                return ResHelper.success("批量重置密码成功");
            }
        }
        return ResHelper.error("批量重置密码失败");
    }

    @PostMapping(value = "/logout")
    public ResHelper<Void> logout(@RequestHeader(value = "ticket") String ticket) {
        Customer customer = customerService.queryCustomerByTicket(ticket);
        if (customer != null) {
            if (customerService.logout(customer) > 0) {
                return ResHelper.success("登出成功");
            }
        }
        return ResHelper.error("登出失败");
    }

}
