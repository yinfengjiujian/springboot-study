package com.neusoft.study.service;

import com.neusoft.study.redis.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Title: com.neusoft.study.service</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/25 0025 7:39
 * Description: 互斥锁的方案
 * 解决缓存雪崩后，所有并发请求全部发送到数据库而导致错误，通过互斥锁，将某一个线程拿到锁的进行数据库操作，其他的
 * 线程全部进行等待，拿到锁的那个线程进行数据库操作查询，更新缓存后，其他线程从缓存中获取数据，从而解决问题。
 */
@Service
@Slf4j
public class TicketServiceLock {


    @Autowired
    private RedisServiceUtil redisServiceUtil;

    //现在JAVA里最常用的锁
    Lock lock = new ReentrantLock();

    @PostConstruct
    public void init(){
        log.info("初始化相关请求");

        /**  redis中 二进制数组的维护实现
         *
         * 在redis中维护一个二进制数组，此数组一般在系统初始化时，将所有记录都加载出来，并将每一条记录对应的bitmaps位置
         * 设置为1,生产环境中，如果数据记录有改变，需要不断的维护此二进制数组(一般都是用某个商品的主键进行hash计算)
         *
         * **/

        //一个元素被标志进入二进制数组的步骤
        String resultStr = "G296";

        //1、拿到二进制数据的长度,redis中是这样
        double size = Math.pow(2, 32);
        //2、计算某一个元素（resultStr）在二进制数组中的具体下标位置
        // (通常这一步在实际开发中会循环hash某一列数据维护进入二进制数组中)
        long index = (long) Math.abs(resultStr.hashCode() % size);
        //3、设置redis二进制数据中的的index下标的值为1；
        redisServiceUtil.setBit(index);
    }

    public Object queryTicketStock(String ticketSeq) {

        /**
         * 后端请求过滤操作，采用布隆过滤  如果请求的参数商品在数据库中不存在，那么直接返回不往下执行
         * */
        //1、拿到二进制数据的长度,redis中是这样
        double size = Math.pow(2, 32);
        //2、计算某一个元素（resultStr）在二进制数组中的具体下标位置
        long index = (long) Math.abs(ticketSeq.hashCode() % size);
        boolean result = redisServiceUtil.getBit(index);
        if (!result) {
            log.warn("该商品在数据库中不存在，不去查询了，没有通过redis布隆过滤器！");
            //TODO  根据业务场景，做相应的返回
            return null;
        }
        String value;

        //1、先从缓存中取数据
        Object object = redisServiceUtil.get(ticketSeq);
        if (object != null) {
            if (object instanceof String) {
                value = (String) object;
                log.info(Thread.currentThread().getName() + "缓存中取得数据：========>" + value);
                return value;
            }
        }
        //加入锁机制，2000个线程并发过来请求了，只有某一个线程拿到了锁的，才能进行如下代码操作，其他的1999个线程进行等待
        //2000并发过来，能够执行lock()方法 和 unlock()方法之间的代码 只能有一个线程，拿到锁的线程，其他的都是先等待
        lock.lock();
        try {
            //其他等待的1999个线程、先从缓存中取数据
            object = redisServiceUtil.get(ticketSeq);
            if (object != null) {
                if (object instanceof String) {
                    value = (String) object;
                    log.info(Thread.currentThread().getName() + "缓存中取得数据：========>" + value);
                    return value;
                }
            }

            //2、缓存中没有，则从数据库取
//            value = tbTicketMapper.selectByPrimaryKey(ticketSeq).getTicketStock().toString();
            value = "";
            log.warn(Thread.currentThread().getName() + "从数据库中取得数据：=============>" + value);

            //3、塞到缓存中 并设置数据120秒过期时间，一致性
            redisServiceUtil.set(ticketSeq,value,120l);
        } finally {
            //释放锁
            lock.unlock();
        }
        return value;
    }
}