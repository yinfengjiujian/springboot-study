package com.neusoft.study.service;

import com.neusoft.study.redis.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: com.neusoft.study.service</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/25 0025 7:39
 * Description: 双缓存+细粒度锁+降级
 *
 */
@Service
@Slf4j
public class TicketServiceGood {


    @Autowired
    private RedisServiceUtil redisServiceUtil;

    //不局限于形式，可以是redis、set都可以
    //保存每一趟车是否在重构缓存的标志，模拟锁的实现,此HashMap是线程安全的
    ConcurrentHashMap<String,String> mapLock = new ConcurrentHashMap<>();

    public Object queryTicketStock(String ticketSeq) {

        String value = "";

        //1、先从缓存中取数据
        Object object = redisServiceUtil.get(ticketSeq);
        if (object != null) {
            if (object instanceof String) {
                value = (String) object;
                log.info(Thread.currentThread().getName() + "缓存中取得数据：========>" + value);
                return value;
            }
        }

        /**需要重构缓存的标志，默认不需要==========Start**/
        boolean lock = false;

        //如果在mapLock 中不存在对应的车次key数据，那么需要重构缓存  细粒度锁的方式
        //mapLock.putIfAbsent 有两种结果，如果不存在key那么put进去并返回null，如果存在那么获取此key的value进行返回
        lock = mapLock.putIfAbsent(ticketSeq,ticketSeq+"") == null;
        try {
            //为true=不存在此趟车的key数据，那么有且仅有某一个线程拿到锁,查询数据库重构缓存
            if (lock) {
                //先从缓存中取数据
                object = redisServiceUtil.get(ticketSeq);
                if (object != null) {
                    if (object instanceof String) {
                        value = (String) object;
                        log.info(Thread.currentThread().getName() + "缓存中取得数据：========>" + value);
                        return value;
                    }
                }

                //2、缓存中没有，则从数据库取
//                value = tbTicketMapper.selectByPrimaryKey(ticketSeq).getTicketStock().toString();
                value = "";
                log.warn(Thread.currentThread().getName() + "从数据库中取得数据：=============>" + value);

                //3、塞到主缓存中 并设置数据120秒过期时间，一致性
                redisServiceUtil.set(ticketSeq,value,120l);

                //TODO  4、塞到备份缓存中，并且不设置key过期时间，备份缓存永久有效

            //没有拿到锁的线程，全部走此逻辑，进行降级返回
            }else {
                //TODO 1、从备份缓存中获取数据进行返回

                //2、返回固定值，进行降级处理
                value = "0";
                log.info( Thread.currentThread().getName() + "缓存降级，返回固定值：" + value);
            }
        }finally {
            //释放模拟伪锁
            if (lock) {
                mapLock.remove(ticketSeq);
            }
        }
        /**需要重构缓存的标志，默认不需要==========End**/
        return value;
    }
}
