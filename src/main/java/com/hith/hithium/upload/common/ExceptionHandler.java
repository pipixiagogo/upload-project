package com.hith.hithium.upload.common;

import com.hith.hithium.upload.utils.ResHelper;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice(basePackages = {"com.hith.hithium.upload.controller"})
public class ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @ResponseStatus(HttpStatus.OK)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public ResHelper<Void> handleRRException(Exception e) {
        logger.error(e.getMessage(),e);
        return ResHelper.error("系统繁忙");
    }
    @ResponseStatus(HttpStatus.OK)
    @org.springframework.web.bind.annotation.ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
    public ResHelper<Void> handleAuthorizationException(UnauthorizedException e){
        logger.error("访问接口无权限:{},访问接口的方法为:{}",e.getMessage(),e.getCause().getMessage());
        return ResHelper.error(HttpStatus.FORBIDDEN.value(),"没有权限，请联系管理员授权");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({MaxUploadSizeExceededException.class})
    public @ResponseBody ResHelper<Void> handleSizeLimitException(MaxUploadSizeExceededException e) {
        logger.error("上传文件太大文件,异常信息为:{}", e.getMessage());
        return ResHelper.error("上传单个文件大小超过"+maxFileSize+"限制/上传总文件大小超过"+maxRequestSize+"限制");
    }

}
