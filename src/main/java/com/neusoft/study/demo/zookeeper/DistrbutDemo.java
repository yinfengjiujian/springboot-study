package com.neusoft.study.demo.zookeeper;

import java.util.concurrent.CountDownLatch;

/**
 * <p>Title: com.neusoft.study.demo.zookeeper</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/28 0028 21:27
 * Description: No Description
 */
public class DistrbutDemo {

    private static final int threadSize = 20;

    private static CountDownLatch countDownLatch = new CountDownLatch(threadSize);

    public static void main(String[] args) {

        Thread[] threads = new Thread[threadSize];

        //模拟多个并发，创建订单号
        for (int i = 0; i < threadSize; i++) {

            Thread thread = new Thread(new OrderCodeThread());

            thread.start();

            countDownLatch.countDown();
        }
    }

    public static class OrderCodeThread implements Runnable{

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new OrderServiceImpl().createOrder();
        }
    }
}
