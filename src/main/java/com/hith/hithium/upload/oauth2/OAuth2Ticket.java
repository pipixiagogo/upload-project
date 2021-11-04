/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.hith.hithium.upload.oauth2;


import org.apache.shiro.authc.AuthenticationToken;

/**
 * token
 *
 */
public class OAuth2Ticket implements AuthenticationToken {
    private String ticket;

    public OAuth2Ticket(String ticket){
        this.ticket = ticket;
    }

    @Override
    public String getPrincipal() {
        return ticket;
    }

    @Override
    public Object getCredentials() {
        return ticket;
    }
}
