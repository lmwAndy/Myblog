package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.SysUserMapper;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.LoginUserVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private LoginService loginService;
    @Override
    public SysUser findUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user==null){
            user=new SysUser();
            user.setNickname("张海柱");
        }
        return user;
    }

    /**
     * 用户登陆验证
     * @param account
     * @param password
     * @return
     */
    @Override
    public SysUser findLoginUser(String account, String password) {
        LambdaQueryWrapper<SysUser> wrapper=new LambdaQueryWrapper();
        wrapper.eq(SysUser::getAccount,account);
        wrapper.eq(SysUser::getPassword,password);
        //有些数据不需要,用多少查多少
        wrapper.select(SysUser::getId,SysUser::getAccount,SysUser::getPassword,SysUser::getAvatar,SysUser::getNickname);
        wrapper.last("limit 1");
        SysUser user = userMapper.selectOne(wrapper);
        return user;
    }

    /**
     * 根据token来获取用户信息
     * 1.token合法性校验,在LoginService中写
     *   是否为空，解析是否成功，redis是否存在
     * 2.如果失败，返回错误
     * 3.如果成功，返回对应结果， LoginUserVo
     *
     * @param token
     * @return
     */
    @Override
    public Result findUserByToken(String token) {

        //checkToken 返回的是存在token中的对象
        SysUser sysUser=loginService.checkToken(token);
        if (sysUser==null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());

        }
        LoginUserVo loginUserVo=new LoginUserVo();
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        return Result.success(loginUserVo);
    }

    /**
     * 用户注册，通过account来查用户
     * @param account
     * @return
     */

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> wrapper=new LambdaQueryWrapper();
        wrapper.eq(SysUser::getAccount,account);

        wrapper.last("limit 1");
        SysUser sysUser = userMapper.selectOne(wrapper);
        return sysUser;

    }

    /**
     * 评论展示，通过作者id来获取作者信息，返回UserVO对象
     * @param authorId
     * @return
     */
    @Override
    public UserVo findUserVoById(Long authorId) {
        SysUser user = userMapper.selectById(authorId);
        if (user==null){
            user=new SysUser();
            user.setId(1L);
            user.setAvatar("/static/img/logo.b3a48c0.png");
            user.setNickname("张海柱");
        }
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(user,userVo);

        userVo.setId(String.valueOf(user.getId()));
        return userVo;
    }

    /**
     * 注册之后，保存用户信息
     * @param sysUser
     */
    @Override
    public void save(SysUser sysUser) {
        //注意 默认生成的id 是分布式id 采用了雪花算法
        userMapper.insert(sysUser);
    }


}
