package com.mszlu.blog.handler;

import com.alibaba.fastjson.JSON;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j//日志处理
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //在controller方法（Handler处理器)执行方法之前执行
        /**
         * 1.需要判断 请求的接口路径 是否为HandelerMethod(controller的方法)
         * 2.判断token是否为空，空就是没有登录
         * 3.如果不为空，登录验证 loginservice checkToken
         * 4.如果验证成功，放行
         *
         */
        if(!(handler instanceof HandlerMethod)){
            //handler 可能是访问资源的，RequestResourceHandle,springboot 程序 访问静态资源
            // 默认去classpath下的static目录去查询，直接放行
            return true;
        }

        String token=request.getHeader("Authorization");

        //加入日志
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if (token == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            //response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        SysUser user = loginService.checkToken(token);
        if (user==null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            //response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSON(result));
            return false;
        }
        //登录验证成功，放行
        //期望在controller中 直接获取用户的信息 怎么获取呢
        UserThreadLocal.put(user);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //如果不删除，ThreadLocal中用完的信息 会有泄露的风险
        UserThreadLocal.remove();
    }
}
