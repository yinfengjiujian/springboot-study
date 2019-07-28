package com.neusoft.study.common.exception;

import com.neusoft.study.common.response.ResponseCodeEnum;
import com.neusoft.study.common.response.ResponseResultUtil;
import com.neusoft.study.common.response.ResponseResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>Title: com.neusoft.study.common.exception</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/27 0027 21:03
 * Description: No Description
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ResponseResultUtil responseResultUtil;

    /**
     * @author
     * @date 2018-8-22
     * @param e     异常
     * @description 处理所有不可知的异常
     */
    @ExceptionHandler({Exception.class})    //申明捕获那个异常类
    @ResponseBody
    public ResponseResultVO globalExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return responseResultUtil.errorEnum(ResponseCodeEnum.OPERATE_FAIL);
    }

    /**
     * @author
     * @date 2018-8-21
     * @param e 异常
     * @description 处理所有业务异常
     */
    @ExceptionHandler({BusinessException.class})
    @ResponseBody
    public ResponseResultVO BusinessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(),e);
        return responseResultUtil.error(e.getCode(), e.getMessage());
    }

}
