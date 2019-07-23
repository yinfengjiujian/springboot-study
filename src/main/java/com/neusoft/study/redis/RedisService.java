package com.neusoft.study.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: com.neusoft.study.redis</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/22 0022 18:29
 * Description: No Description
 */
@Component
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //这样该方法支持多种数据类型
    public void set(String key , Object object, Long time){
        if (object instanceof String ) {  //判断下是String类型不
            String argString =(String)object;  //强转下
            //存放String类型的
            stringRedisTemplate.opsForValue().set(key, argString);
        }
        //如果存放Set类型
        if (object instanceof Set) {
            Set<String> valueSet =(Set<String>)object;
            for(String string:valueSet){
                stringRedisTemplate.opsForSet().add(key, string);  //此处点击下源码看下 第二个参数可以放好多
            }
        }
        //设置有效期
        if (time != null) {
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }

    }
    //做个封装
    public void setString(String key, Object object){
        String argString =(String)object;  //强转下
        //存放String类型的
        stringRedisTemplate.opsForValue().set(key, argString);
    }
    public void setSet(String key, Object object){
        Set<String> valueSet =(Set<String>)object;
        for(String string:valueSet){
            stringRedisTemplate.opsForSet().add(key, string);  //此处点击下源码看下 第二个参数可以放好多
        }
    }

    public String getString(String key){
        return    stringRedisTemplate.opsForValue().get(key);
    }
}
