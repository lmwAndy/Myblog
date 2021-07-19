package com.mszlu.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

//统一的参数返回类
@Data
@AllArgsConstructor
public class Result {

    private boolean success;

    private int code;

    private  String msg;

    private Object data;

    //开发静态方法，便于我们构建result
    public static Result success(Object data){
        return new Result(true,200,"success",data);
    }

    //如果失败返回的静态结果对象
    public static Result fail(int code,String msg){
        return new Result(false,code,msg,null);
    }

}
