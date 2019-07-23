package com.neusoft.study.demo.zookeeper;

import com.neusoft.study.utils.MyZKSerializer;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * <p>Title: com.neusoft.study.demo</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/28 0028 20:29
 * Description: zookeeper客户端监听demo
 */
@Slf4j
public class ZKWatcherDemo {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("192.168.86.128:2181");
        zkClient.setZkSerializer(new MyZKSerializer());

        zkClient.subscribeDataChanges("/mike", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object object) throws Exception {
                log.info("监听到数据变为：" + object);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                log.info("监听到数据节点删除了");
            }
        });

        try {
            Thread.sleep(5*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
