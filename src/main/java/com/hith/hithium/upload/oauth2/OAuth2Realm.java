
package com.hith.hithium.upload.oauth2;

import com.hith.hithium.upload.entity.Customer;
import com.hith.hithium.upload.service.CustomerService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 认证
 * 实现AuthorizingRealm接口用户
 */
@Component
public class OAuth2Realm extends AuthorizingRealm implements BeanPostProcessor {


    @Resource @Lazy
    private CustomerService customerService;

    @Override
    public boolean supports(AuthenticationToken ticket) {
        return ticket instanceof OAuth2Ticket;
    }

    /**
     * 授权(验证权限时调用)  登录成功调用该方法授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Customer userPrincipal = (Customer) principals.getPrimaryPrincipal();
        //授权过程 用户权限列表
        Set<String> permsSet = customerService.queryPermissions(userPrincipal);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     * 调用subject.login(token);  则会调用 doGetAuthenticationInfo 进行登录
     * <p>
     * 登录失败后会调用  onLoginFailure 进行失败处理
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //得到token
        String accessTicket = (String) token.getPrincipal();
        // 根据accessToken，查询用户信息
        Customer customer = customerService.queryCustomerByTicket(accessTicket);
        //token失效
        if (customer == null) {
            throw new IncorrectCredentialsException("无效令牌,请重新登录");
        }
        if (customer.getTicketExpireTime().getTime() < System.currentTimeMillis()) {
            throw new IncorrectCredentialsException("令牌过期,请重新登录");
        }
        return new SimpleAuthenticationInfo(customer, accessTicket, getName());
    }
}
