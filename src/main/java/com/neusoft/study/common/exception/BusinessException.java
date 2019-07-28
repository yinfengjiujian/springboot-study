package com.neusoft.study.common.exception;

import com.neusoft.study.common.response.ResponseCodeEnum;

/**
 * <p>Title: com.neusoft.study.common.exception</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/27 0027 20:09
 * Description: No Description
 */
public class BusinessException extends RuntimeException {

    private Integer code; //错误码

    public BusinessException() {

    }

    public BusinessException(ResponseCodeEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
