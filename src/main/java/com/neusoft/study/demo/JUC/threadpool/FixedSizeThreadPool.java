package com.neusoft.study.demo.JUC.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>Title: com.neusoft.study.demo.JUC.threadpool</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/9 0009 16:38
 * Description: 固定大小的线程池
 */
public class FixedSizeThreadPool {

    //仓库
    private BlockingQueue<Runnable> taskQueue;

    //工作线程
    private List<Thread> workers;

    //线程池工作标识
    private volatile boolean working = true;


    //1、完成池的构成元素初始化操作
    /**
     * @param poolSize   线程池大小
     * @param taskQueueSize     任务队列大小
     */
    public FixedSizeThreadPool(int poolSize,int taskQueueSize) {

        if (poolSize <= 0 || taskQueueSize <= 0){
            throw new IllegalArgumentException("参数错误，返回");
        }

        //初始化仓库
        taskQueue = new LinkedBlockingQueue<>(taskQueueSize);

        //初始化poolSize工作线程数量
        this.workers = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker(this);
            worker.start();
            this.workers.add(worker);
        }

    }

    //2、完成提交任务的方法
    public boolean submit(Runnable task){
        //如果线程池工作标识为true那么允许添加任务
        if (this.working) {
            return this.taskQueue.offer(task);
        }
        return false;
    }

    //4、实现关闭功能
    public void shutdown(){
        this.working = false;

        //把阻塞的线程进行中断；
        for (Thread thread : this.workers){
            if (thread.getState().equals(Thread.State.BLOCKED) ||
                    thread.getState().equals(Thread.State.WAITING) ||
                            thread.getState().equals(Thread.State.TIMED_WAITING)) {
                thread.interrupt();
            }
        }
    }


    /**
     * 具体的工作线程类
     */
    private static class Worker extends Thread{

        private FixedSizeThreadPool threadPool;

        public Worker(FixedSizeThreadPool threadPool) {
            super();
            this.threadPool = threadPool;
        }

        public void run(){

            //工作任务统计
            int taskCount = 0;

            while (this.threadPool.working || this.threadPool.taskQueue.size() > 0){
                Runnable task = null;

                try {
                    //3、完成线程池取任务、执行----->取任务
                    //3.1如果线程池还在工作则采用获取任务时阻塞的方式获取
                    if (this.threadPool.working) {
                        task = this.threadPool.taskQueue.take();
                    }else {
                        //3.1如果线程池不再工作则采用获取任务时非阻塞的方式获取
                        task = this.threadPool.taskQueue.poll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (task != null) {
                    //3、完成线程池取任务、执行----->执行
                    task.run();
                    System.out.println(Thread.currentThread().getName()
                            + "执行完" + (++taskCount) + "个任务！");
                }
            }
            System.out.println(Thread.currentThread().getName() + "结束");
        }
    }

    public static void main(String[] args) {
        FixedSizeThreadPool fixedSizeThreadPool = new FixedSizeThreadPool(3,6);

        for (int i = 0; i < 8; i++) {
            fixedSizeThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("任务开始执行。。。。。。");
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //关闭线程
        fixedSizeThreadPool.shutdown();
    }


}
