package com.neusoft.study.user.service.impl;

import com.neusoft.study.common.exception.BusinessException;
import com.neusoft.study.common.response.ResponseCodeEnum;
import com.neusoft.study.common.shiro.utils.JwtUtils;
import com.neusoft.study.redis.RedisServiceUtil;
import com.neusoft.study.user.entity.UserInfo;
import com.neusoft.study.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>Title: com.neusoft.study.user.service.impl</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/27 0027 16:42
 * Description: No Description
 */
@Service
public class UserJwtServiceImpl {

    @Autowired
    @Qualifier("userServiceImpl")
    private IUserService userService;

    @Autowired
    private RedisServiceUtil redisServiceUtil;


    /**
     * 清除token信息
     *
     * @param account 登录用户名
     */
    public void deleteLoginInfo(String account) {
        //删除数据库或者缓存中保存的salt
        redisServiceUtil.del("token:" + account);
    }

    /**
     * 保存user登录信息，返回token
     *
     * @param
     */
    public String generateJwtToken(String account) {
        //生成随机盐，用于放入redis中，生成token需要的秘钥
        String salt = JwtUtils.generateSalt();
        //将salt保存到数据库或者缓存中
        redisServiceUtil.set("token:" + account,salt,3600);
        return JwtUtils.sign(account, salt, 3600); //生成jwt token，设置过期时间为1小时
    }

    /**
     * 获取上次token生成时的salt值和登录用户信息
     *
     * @param account
     * @return
     */
    public UserInfo getJwtTokenInfo(String account) {
        //从数据库或者缓存中取出jwt token生成时用的salt
        String salt = "";
        Object object = redisServiceUtil.get("token:" + account);
        if (!ObjectUtils.isEmpty(object)){
            salt = (String) object;
        }else {
            throw new BusinessException(ResponseCodeEnum.TOKEN_ERROR);
        }
        UserInfo userInfo = userService.getUserInfo(account);
        userInfo.setTokenSalt(salt);
        return userInfo;
    }
}
