package com.cosmos.friendsMatching.pojo;


import com.cosmos.friendsMatching.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code;
    private String msg;
    private Object data;
    private String description;

    //请求成功无数据

    /**
     * 返回成功的信息，无数据
     *
     * @param msg 请求成功的信息，无数据
     * @return 返回成功
     */
    public static Result success(String msg) {
        return new Result(200, msg, null, "");
    }


    /**
     * 请求成功有数据
     *
     * @param msg  请求成功的信息
     * @param data 请求成功的数据
     * @return 返回成功
     */
    //请求成功有数据
    public static Result success(String msg, Object data) {
        return new Result(200, msg, data, "");
    }


    //定义两个请求失败，一个直接传入通用的枚举类型的错误

    /**
     * 用于返回枚举类型通用错误
     *
     * @param errorCode 枚举类型通用错误
     * @return 返回错误
     */
    public static Result error(ErrorCode errorCode) {
        return new Result(errorCode.getCode(), errorCode.getMsg(), null, "");
    }


    //一个传入自定义的错误信息

    /**
     * 用于返回自定义的错误信息
     *
     * @param msg 自定义错误信息
     * @return 返回错误
     */
    public static Result error(String msg) {
        return new Result(500, msg, null, "");
    }


    public static Result error(int code, String msg, String description) {

        return new Result(code, msg, null, description);
    }


}
