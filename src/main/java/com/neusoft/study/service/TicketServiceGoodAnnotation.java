package com.neusoft.study.service;

import com.neusoft.study.common.cache.AierCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class TicketServiceGoodAnnotation {


    /**
     * 利用EL表达式，将方法的运行参数值（ticketSeq）动态的传入缓存组件中
     * @param ticketSeq
     * @return
     */
    @AierCache(keyEL = "#ticketSeq")
    public Object queryTicketStock(String ticketSeq) {
//        return tbTicketMapper.selectByPrimaryKey(ticketSeq).getTicketStock().toString();

        return null;
    }
}
