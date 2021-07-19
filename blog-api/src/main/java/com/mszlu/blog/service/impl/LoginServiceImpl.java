package com.mszlu.blog.service.impl;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.utils.JWTUtils;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    //加密盐
    private static final String slat = "mszlu!@#";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public Result login(LoginParams loginParams) {
        /**
         * 1.检查检查参数是否合法
         * 2.根据用户名和密码去user表中查询 是否纯在
         * 3.如果不存在，则登录失败
         * 4.存在，使用jwt生成token返回前端
         * 5.token放入redis当中，redis  token：user信息 设置过期时间
         * （登录认证使得时候，先认证字符串是否合法，
         * 去reids认证是否存在）
         */
        String account=loginParams.getAccount();
        String password=loginParams.getPassword();
        if(StringUtils.isBlank(account)||StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        //给密码加密
        password = DigestUtils.md5Hex(loginParams.getPassword()+slat);
        //查询用户交给sysUserService来处理
        SysUser loginUser=sysUserService.findLoginUser(account,password);
        if (loginUser==null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        System.out.println(loginUser.getId());
        System.out.println("--------");
        String token = JWTUtils.createToken(loginUser.getId());

        System.out.println(loginUser.getId());
        System.out.println(token.toString());
        //把token放入redis中//放入User，把USer转换成Json格式，加上过期时间
        redisTemplate.opsForValue().set("TOKEN"+token, JSON.toJSONString(loginUser),1,TimeUnit.DAYS);

        return Result.success(token);
    }

    /**
     * 检查token是否合法，从Redis中取出用户数据返回
     * @param token
     * @return
     */
    @Override
    public SysUser checkToken(String token) {
        //看token是否为空
        if (StringUtils.isBlank(token)){
            return null;
        }

        //检查token的合法性,返回一个map集合
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap==null){
            return null;
        }

        //如果不为空，则合法，去Redis中获取用户信息
        String UserJson = redisTemplate.opsForValue().get("TOKEN" + token);
        if (UserJson==null){
            //过期了，返回空
            return null;
        }
        //把json解析为user对象
        SysUser sysUser = JSON.parseObject(UserJson, SysUser.class);
        return sysUser;
    }

    /**
     * 退出登录，删除redis里的用户token
     * @param token
     * @return
     */
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN"+token);
        return Result.success(null);
    }

    /**
     * 用户注册
     * /**
     *           1.检查检查参数是否合法
     *          2.根据用户名和密码去user表中查询 是否纯在
     *           3.如果不存在，则进行注册
     *          4.存在，则失败，返回账户已存在
     *
     *          5.生成token，token放入redis当中，redis  token：user信息 设置过期时间
     *          （登录认证使得时候，先认证字符串是否合法，
     *          去reids认证是否存在）
     *          6.加上事务，一旦中间任何过程出现问题， 需要回滚
     *
     *
     * @param loginParams
     * @return
     */
    @Override
    public Result register(LoginParams loginParams) {

        String account=loginParams.getAccount();
        String password = loginParams.getPassword();
        String nickname = loginParams.getNickname();
        if (StringUtils.isBlank(account)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(nickname)
                ){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }

        //查询用户交给sysUserService来处理
        SysUser sysUser = this.sysUserService.findUserByAccount(account);
        if (sysUser!=null){
            //如果存在则返回错误信息
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());
        }

        //如果用户不存在,则创建一个注册用户
        sysUser=new SysUser();
        sysUser.setAccount(account);
        sysUser.setPassword(password);
        sysUser.setNickname(nickname);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        //生成token
        String token = JWTUtils.createToken(sysUser.getId());
        //存入Redis,设置保留时间
        redisTemplate.opsForValue().set("TOKEN"+token,JSON.toJSONString(sysUser),1,TimeUnit.DAYS);
        return Result.success(token);

    }

    public static void main(String[] args) {
        LoginParams loginParams=new LoginParams();

    }


}
