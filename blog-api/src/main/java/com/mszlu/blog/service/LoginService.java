package com.mszlu.blog.service;

import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParams;

public interface LoginService {

    /**
     * 用户登录
     * @param loginParams
     * @return
     */
     Result login(LoginParams loginParams);

    /**
     * 检查TOKEN是否合法
     * @param token
     * @return
     */
    SysUser checkToken(String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 用户注册
     * @param loginParams
     * @return
     */
    Result register(LoginParams loginParams);
}
