package com.neusoft.study;

import com.neusoft.study.service.TicketService;
import com.neusoft.study.service.TicketServiceGood;
import com.neusoft.study.service.TicketServiceGoodAnnotation;
import com.neusoft.study.service.TicketServiceLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * <p>Title: com.neusoft.study</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/25 0025 7:52
 * Description: No Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TicketTest {

    //没有优化前的service层
    @Autowired
    private TicketService ticketService;

    //优化后，加入了锁的service层
    @Autowired
    private TicketServiceLock ticketServiceLock;

    @Autowired
    private TicketServiceGood ticketServiceGood;

    @Autowired
    private TicketServiceGoodAnnotation ticketServiceGoodAnnotation;

    //车次
    private static final String TICKET_SEQ = "G296";

    //模拟并发的请求数量
    private static final int threadNum = 200;

    //倒计数器，用于模拟高并发（信号枪机制）
    private CountDownLatch countDownLatch = new CountDownLatch(threadNum);

    long timed = 0L;

    @Before
    public void start(){
        log.info("开始测试！");
        timed = System.currentTimeMillis();
    }

    @After
    public void end(){
        log.info("结束测试，执行时长：" + (System.currentTimeMillis() - timed));
    }

    @Test
    public void benchmark() throws InterruptedException {

        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(new QueryRequest());
            threads[i] = thread;

            thread.start();
            //倒计器倒计数减一
            countDownLatch.countDown();
        }

        //等待上面所有的线程执行完毕之后，结束测试
        for (Thread thread : threads){
            thread.join();
        }
    }

    private class QueryRequest implements Runnable{

        @Override
        public void run() {
            try {
                //阻塞线程，等待全部线程准备完毕
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //http请求，实际就是多个线程同时调用这个方法
//            ticketService.queryTicketStock(TICKET_SEQ);

            //加入锁机制后的操作类
//            ticketServiceLock.queryTicketStock(TICKET_SEQ);

            //双缓存机制 + 细粒度锁 + 降级
//            ticketServiceGood.queryTicketStock(TICKET_SEQ);

            //采用封装好的缓存注解组件来处理缓存
            ticketServiceGoodAnnotation.queryTicketStock(TICKET_SEQ);
        }
    }

}
