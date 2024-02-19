package com.phemex.dataFactory.request;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Date: 2022年04月17日 19:30
 * @Description:
 */
public class ResultHolder {
    public ResultHolder() {
        this.success = true;
    }

    private ResultHolder(Object data) {
        this.data = data;
        this.success = true;
    }

    private ResultHolder(boolean success, String msg) {
        this.success = success;
        this.message = msg;
    }

    private ResultHolder(boolean success, String msg, Object data) {
        this.success = success;
        this.message = msg;
        this.data = data;
    }

    // 请求是否成功
    private boolean success = false;
    // 描述信息
    private String message;
    // 返回数据
    private Object data = "";

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ResultHolder success(Object data) {
        return new ResultHolder(true, "OK", data);
    }

    public static ResultHolder success(String msg, Object data) {
        return new ResultHolder(true, msg, data);
    }

    public static ResultHolder error(String message) {
        return new ResultHolder(false, message, null);
    }

    public static ResultHolder error(String message, Object object) {
        return new ResultHolder(false, message, object);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
