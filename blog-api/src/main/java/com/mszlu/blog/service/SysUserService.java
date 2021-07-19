package com.mszlu.blog.service;

import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.UserVo;

public interface SysUserService {
    SysUser findUserById(Long id);


    /**
     * 用户登陆验证
     * @param account
     * @param password
     * @return
     */
    SysUser findLoginUser(String account, String password);

    /**
     * 用户登陆之后，获取用户信息
     * @param token
     * @return
     */
    Result findUserByToken(String token);


    /**
     * 用户注册，通过用户名看注册用户是否已经存在
     * @param account
     * @return
     */
    SysUser findUserByAccount(String account);



    /**
     * 展示评论，用文章表的作者id查询作者
     * @param authorId
     * @return
     */
    UserVo findUserVoById(Long authorId);


    /**
     * 注册之后，保存用户信息
     * @param sysUser
     */
    public void save(SysUser sysUser);



}
