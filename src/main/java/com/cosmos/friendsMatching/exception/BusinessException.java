package com.cosmos.friendsMatching.exception;


import com.cosmos.friendsMatching.common.ErrorCode;
import lombok.Getter;

/**
 * 定义业务异常，继承RuntimeException来实现自定义异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final  String msg;
    private final String description;


    public BusinessException(int code, String msg, String description) {
        super(msg);
        this.code = code;
        this.msg=msg;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode,  String description) {
        super(errorCode.getMsg());  //将errorCode定义好的message传入
        this.msg=errorCode.getMsg();
        this.code = errorCode.getCode();
        this.description = description;

    }



}
