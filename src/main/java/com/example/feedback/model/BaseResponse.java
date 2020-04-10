package com.example.feedback.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {
    /**
     * 0代表正确，非0代表不正确
     */
    private int code;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 返回信息
     */
    private Object data;

    public static BaseResponse success() {
        BaseResponse response = new BaseResponse();
        response.setCode(0);
        response.setMessage("ok");
        return response;
    }

    public static BaseResponse success(Object data) {
        BaseResponse response = new BaseResponse();
        response.setCode(0);
        response.setMessage("ok");
        response.setData(data);
        return response;
    }

    public static BaseResponse fail(String message) {
        BaseResponse response = new BaseResponse();
        response.setCode(1);
        response.setMessage(message);
        return response;
    }
}
