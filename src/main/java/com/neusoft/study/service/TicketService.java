package com.neusoft.study.service;

import com.neusoft.study.redis.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Title: com.neusoft.study.service</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/25 0025 7:39
 * Description: No Description
 */
@Service
@Slf4j
public class TicketService {

//    @Autowired
//    private TbTicketMapper tbTicketMapper;

    @Autowired
    private RedisServiceUtil redisServiceUtil;

    public Object queryTicketStock(String ticketSeq) {

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

        //2100并发
        //2、缓存中没有，则从数据库取
//        value = tbTicketMapper.selectByPrimaryKey(ticketSeq).getTicketStock().toString();
        value = "";
        log.warn(Thread.currentThread().getName() + "从数据库中取得数据：=============>" + value);

        //3、塞到缓存中 并设置数据120秒过期时间，一致性
        redisServiceUtil.set(ticketSeq,value,120l);

        return value;
    }
}
