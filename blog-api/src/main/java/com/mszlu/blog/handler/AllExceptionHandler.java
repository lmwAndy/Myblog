package com.mszlu.blog.handler;

import com.mszlu.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//对加了Controller的方法进行拦截处理，AOP的实现
public class AllExceptionHandler {
    //进行异常处理，处理Exception.class的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody//不加这个返回页面了，加了返回json数据
    public Result doException(Exception ex){
        //简单打印到堆栈,后续会加入到日志
        ex.printStackTrace();
        return Result.fail(-999,"系统异常");
    }
}
