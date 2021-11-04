/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.hith.hithium.upload.oauth2;

import com.alibaba.fastjson.JSONObject;
import com.hith.hithium.upload.utils.HttpContextUtils;
import com.hith.hithium.upload.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * oauth2过滤器
 * 调用接口时，接受传过来的token后，保证token有效及用户权限呢
 */
public class OAuth2Filter extends AuthenticatingFilter {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Filter.class);

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String ticket = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(ticket)) {
            return null;
        }
        return new OAuth2Ticket(ticket);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return ((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());
            String json = JSONObject.toJSONString(ResHelper.result(HttpStatus.UNAUTHORIZED.value(), "无效token,请重新登录"));
            httpResponse.getWriter().print(json);
            return false;
        }
        return executeLogin(request, response);
    }

    /**
     * 登录失败后 调用该方法
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            String json = JSONObject.toJSONString(ResHelper.result(HttpStatus.UNAUTHORIZED.value(), throwable.getMessage()));
            httpResponse.getWriter().print(json);
        } catch (IOException e1) {
            log.error("处理登录失败异常", e);
        }
        return false;
    }

    /**
     * 获取请求的token
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        String ticket = httpRequest.getHeader("ticket");
        if (StringUtils.isBlank(ticket)) {
            ticket = httpRequest.getParameter("ticket");
        }
        return ticket;
    }


}
