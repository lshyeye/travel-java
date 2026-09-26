package com.example.traveljava.vo;


import lombok.Data;

@Data
// 定义返回值类型
public class Result<T> {
//    T 代表泛型，传入这个数据
    private  Boolean success;
    private  Integer code;
    private  String message;
    private  T data;
    private  String error; // 错误的内容
    private  String rawResponse; // 原始响应内容

    // 构造方法


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    // 构造方法
    public static <T> Result<T> ok( ){
        Result<T> result = new Result<T>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("成功");

        return result;
    }


//    方法的重载
    public static <T> Result<T> ok( T data){
        Result<T> result = ok();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(  ){
        Result<T> result = new Result<T>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage("失败");
        return result;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = fail();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(  ) {
        Result<T> result = new Result<T>();
        result.setSuccess(false);
        return result;
    }


    public static <T> Result<T> error(String error, String rawResponse) {
        Result<T> result = new Result<T>();
        result.setSuccess(false);
        result.setError(error);
        result.setRawResponse(rawResponse);
        return result;
    }

}
