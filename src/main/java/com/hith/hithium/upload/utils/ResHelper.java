package com.hith.hithium.upload.utils;


import com.hith.hithium.upload.common.Const;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;


@Data
public class ResHelper<T> {
    private static final Logger log = LoggerFactory.getLogger(ResHelper.class);
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;
    //1:成功
    //0：失败
    private int code;

    private String msg;

    private T data;

    private boolean success;

    public static ResHelper pamIll() {
        return ResHelper.error(Const.PARAM_ILL);
    }


    private ResHelper() {
    }
    public static ResHelper error(Integer code,String msg){
        ResHelper helper = new ResHelper();
        helper.setCode(code);
        helper.setMsg(msg);
        helper.setSuccess(false);
        return helper;
    }

    public static <S> ResHelper<S> success(String msg, S s) {
        ResHelper<S> helper = new ResHelper();
        helper.setCode(SUCCESS);
        helper.setData(s);
        helper.setMsg(msg);
        helper.setSuccess(true);
        return helper;
    }

    public static ResHelper success(String msg) {
        ResHelper helper = new ResHelper();
        helper.setCode(SUCCESS);
        helper.setMsg(msg);
        helper.setSuccess(true);
        return helper;
    }

    public static ResHelper error(String msg) {
        ResHelper helper = new ResHelper();
        helper.setCode(ERROR);
        helper.setMsg(msg);
        helper.setSuccess(false);
        return helper;
    }

    public static ResHelper result(Integer code, String msg) {
        ResHelper helper = new ResHelper();
        helper.setCode(code);
        helper.setMsg(msg);
        return helper;
    }


}
