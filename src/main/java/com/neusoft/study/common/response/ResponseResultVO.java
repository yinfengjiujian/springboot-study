package com.neusoft.study.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>Title: com.neusoft.study.common.response</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/27 0027 21:59
 * Description: No Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResultVO<T> implements Serializable {

    /**
     * @description 响应码
     */
    private int code;

    /**
     * @description 响应消息
     */
    private String message;

    /**
     * @description 数据
     */
    private T data;
}
