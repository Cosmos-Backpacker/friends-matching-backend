package com.cosmos.friendsMatching.exception;


import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(BusinessException e) {
        log.error("businessException:Code:{} Message:{} Description{}", e.getCode(), e.getMessage(), e.getDescription());
        return Result.error(e.getCode(), e.getMsg(), e.getDescription());
    }



    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException{}", e.getMessage());
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }

}
