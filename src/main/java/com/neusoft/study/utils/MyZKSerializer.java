package com.neusoft.study.utils;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: com.neusoft.study.utils</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/28 0028 20:31
 * Description: No Description
 */
public class MyZKSerializer implements ZkSerializer {

    String chartset = "UTF-8";

    @Override
    public byte[] serialize(Object object) throws ZkMarshallingError {
        try {
            return String.valueOf(object).getBytes(chartset);
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return new String(bytes,chartset);
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }
    }
}
