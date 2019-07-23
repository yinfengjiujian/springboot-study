package com.neusoft.study.demo.zookeeper;

import java.util.concurrent.locks.Lock;

/**
 * <p>Title: com.neusoft.study.demo.zookeeper</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/28 0028 21:14
 * Description: No Description
 */
public class OrderServiceImpl implements OrderService {

    private static OrderCodeGenerator codeGenerator = new OrderCodeGenerator();

    private static Lock lock = new ZKDistrbuteImproveLock("/duanml");

    @Override
    public void createOrder() {

        String orderCode = null;

//        synchronized (codeGenerator){
//            orderCode = codeGenerator.getOrderCode();
//        }

        lock.lock();
        try {
            orderCode = codeGenerator.getOrderCode();
        } finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName() + "=======" + orderCode);

    }
}
