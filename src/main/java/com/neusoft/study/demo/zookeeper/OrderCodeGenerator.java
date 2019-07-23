package com.neusoft.study.demo.zookeeper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title: com.neusoft.study.demo.zookeeper</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/28 0028 21:06
 * Description: No Description
 */
public class OrderCodeGenerator {

    private static int anInt = 0;

    public String getOrderCode(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        return simpleDateFormat.format(date) + ++anInt;
    }



}
