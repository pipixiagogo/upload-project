package com.hith.hithium.upload.controller;

import com.hith.hithium.upload.service.CustomerService;
import com.hith.hithium.upload.utils.ResHelper;
import com.hith.hithium.upload.vo.CustomerLoginVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LoginController {

    @Resource
    private CustomerService customerService;


    @PostMapping(value = "/customerLogin")
    public ResHelper<CustomerLoginVo> customerLogin(@RequestParam(value = "employeeNum", name = "employeeNum", required = false) String employeeNum,
                                   @RequestParam(value = "password", name = "password", required = false) String password) {
        if (StringUtils.isEmpty(employeeNum) || StringUtils.isEmpty(password)) {
            return ResHelper.error("工号/密码错误");
        }

        CustomerLoginVo customerLoginVo = customerService.customerLogin(employeeNum, password);
        if (customerLoginVo != null) {
            return ResHelper.success("登录成功", customerLoginVo);
        }
        return ResHelper.error("工号/密码错误");
    }

}
