package com.example.feedback.intercepter;

import com.example.feedback.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class ApiControllerAdvice {

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public BaseResponse handleNormalException(HttpServletRequest req, Throwable e) {
        log.error(e.getMessage(), e);
        return BaseResponse.fail(e.getMessage());
    }
}
