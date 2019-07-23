package com.neusoft.study.demo.zookeeper;

import com.neusoft.study.utils.MyZKSerializer;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * <p>Title: com.neusoft.study.demo.zookeeper</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/28 0028 22:36
 * Description: 基于Zookeeper实现的分布式锁
 */
public class ZKDistrbuteImproveLock implements Lock {

    /**
     * 利用Zookeeper的同父子节点不可重名的特性来实现分布式锁
     * 加锁：去创建指定名称的节点，如果创建成功，那么获得锁（即加锁成功），如果创建失败，则节点已经存在，就标识锁已经被人获取了
     * 你就得阻塞等待；
     * 释放锁：删除指定名称节点
     */

    //Zookeeper创建节点的路径目录
    private String lockPath;

    //临时节点的目录
    private ThreadLocal<String> currentPath = new ThreadLocal<>();

    //临时节点的前一个节点路径
    private ThreadLocal<String> beforePath = new ThreadLocal<>();

    private static ZkClient zkClient;

    static {
        zkClient = new ZkClient("192.168.86.128:2181");
        zkClient.setZkSerializer(new MyZKSerializer());
    }

    public ZKDistrbuteImproveLock(String lockPath) {
        this.lockPath = lockPath;
        //创建持久节点
        if (!zkClient.exists(this.lockPath)) {
            zkClient.createPersistent(this.lockPath);
        }
    }

    @Override
    public void lock() {
        //创建节点
        if (!tryLock()) {
            //如果创建失败，那么就阻塞等待
            waitForLock();
            //再次尝试加锁
            lock();
        }
    }

    private void waitForLock() {
        //1、怎么让自己阻塞,利用CountDownLatch让线程阻塞起来
        CountDownLatch countDownLatch = new CountDownLatch(1);

        //2、怎么唤醒线程呢？

        /**  通过Zookeeper的监听支持，时刻监听节点的状态，当节点被删除的时候，就去唤醒线程，继续执行处理 */
        //2.1、注册watcher
        IZkDataListener iZkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                //唤醒线程，继续进行争抢锁，即争抢创建节点的权限。
                countDownLatch.countDown();
                System.out.println("--------监听到节点被删除");
            }
        };

        zkClient.subscribeDataChanges(this.beforePath.get(), iZkDataListener);

        if (zkClient.exists(this.beforePath.get())) {
            try {
                //让线程挂起，阻塞起来
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        zkClient.unsubscribeDataChanges(this.beforePath.get(), iZkDataListener);
    }

    @Override
    public boolean tryLock() {
        //如果临时节点不存在，尝试创建临时节点
        if (this.currentPath.get() == null) {
            currentPath.set(zkClient.createEphemeralSequential(this.lockPath + "/", "aaa"));
        }
        //获取所有的子节点
        List<String> children = zkClient.getChildren(this.lockPath);
        //将返回的List对象进行排序
        Collections.sort(children);

        //判断当前节点是否是最小的
        if (currentPath.get().equals(this.lockPath + "/" + children.get(0))) {
            return true;
        } else {
            //获取前一个节点的路径
            int curIndex = children.indexOf(this.currentPath.get().substring(lockPath.length() + 1));
            beforePath.set(this.lockPath + "/" + children.get(curIndex - 1));
        }
        return false;
    }

    @Override
    public void unlock() {
        //删除临时节点
        zkClient.delete(this.currentPath.get());
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
