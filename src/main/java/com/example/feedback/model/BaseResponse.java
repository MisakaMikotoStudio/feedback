package com.example.feedback.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "返回数据通用格式")
public class BaseResponse implements Serializable {

    @ApiModelProperty(value = "0代表正确，非0代表不正确")
    private int code;

    @ApiModelProperty(value = "返回不正确时的异常信息")
    private String message;

    @ApiModelProperty(value = "返回数据")
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
