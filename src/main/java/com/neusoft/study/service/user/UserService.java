package com.neusoft.study.service.user;

import com.neusoft.study.common.shiro.utils.JwtUtils;
import com.neusoft.study.entity.user.UserDto;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Title: com.neusoft.study.service.user</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/30 0030 11:59
 * Description: No Description
 */
@Service
public class UserService {

    //数据库存储的用户密码的加密salt，正式环境不能放在源代码里
    private static final String encryptSalt = "F12839WhsnnEV$#23b";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 保存user登录信息，返回token
     *
     * @param
     */
    public String generateJwtToken(String username) {
        String salt = "12345";//JwtUtils.generateSalt();
        /**
         * @todo 将salt保存到数据库或者缓存中
         * redisTemplate.opsForValue().set("token:"+username, salt, 3600, TimeUnit.SECONDS);
         */
        return JwtUtils.sign(username, salt, 3600); //生成jwt token，设置过期时间为1小时
    }

    /**
     * 获取上次token生成时的salt值和登录用户信息
     *
     * @param username
     * @return
     */
    public UserDto getJwtTokenInfo(String username) {

        String salt = "12345";
        /**
         * @todo 从数据库或者缓存中取出jwt token生成时用的salt
         * salt = redisTemplate.opsForValue().get("token:"+username);
         */
        UserDto user = getUserInfo(username);
        user.setSalt(salt);
        return user;
    }

    /**
     * 清除token信息
     *
     * @param username 登录用户名
     */
    public void deleteLoginInfo(String username) {
        /**
         * @todo 删除数据库或者缓存中保存的salt
         * redisTemplate.delete("token:"+username);
         */

    }

    /**
     * 获取数据库中保存的用户信息，主要是加密后的密码
     *
     * @param userName
     * @return
     */
    public UserDto getUserInfo(String userName) {
        UserDto user = new UserDto();
        user.setUserId(1L);
        user.setUsername("admin");
        user.setEncryptPwd(new Sha256Hash("123456", encryptSalt).toHex());
        return user;
    }

    /**
     * 获取用户角色列表，强烈建议从缓存中获取
     *
     * @param userId
     * @return
     */
    public List<String> getUserRoles(Long userId) {
        return Arrays.asList("admin");
    }
}
