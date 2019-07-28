package com.neusoft.study.common.response;

import org.springframework.stereotype.Component;

/**
 * <p>Title: com.neusoft.study.common.response</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/27 0027 22:02
 * Description: No Description
 */
@Component
public final class ResponseResultUtil<T> {

    /**
     * @param code      响应码
     * @param message   相应信息
     * @param data       返回的数据
     * @description     请求成功返回对象
     */
    public final ResponseResultVO success(int code, String message, T data) {
        return new ResponseResultVO(code, message, data);
    }

    /**
     * @param data   返回的数据
     * @description 请求成功返回对象
     */
    public final ResponseResultVO success(T data) {
        int code = ResponseCodeEnum.SUCCESS.getCode();
        String message = ResponseCodeEnum.SUCCESS.getMessage();
        return this.success(code, message,data);
    }

//    /**
//     * @param any   返回的数据
//     * @description 请求成功返回对象
//     */
//    public final ResponseResultVO success(Object any, PageVO page) {
//        int code = ResponseCodeEnum.SUCCESS.getCode();
//        String message = ResponseCodeEnum.SUCCESS.getMessage();
//        return this.success(code, message, page, any);
//    }

    /**
     * @description 请求成功返回对象
     */
    public final ResponseResultVO success() {
        return this.success(null);
    }

    /**
     * @param responseCodeEnum  返回的响应码所对应的枚举类
     * @description         请求失败返回对象
     */
    public final ResponseResultVO errorEnum(ResponseCodeEnum responseCodeEnum) {
        return new ResponseResultVO(responseCodeEnum.getCode(), responseCodeEnum.getMessage(), null);
    }

    /**
     * @param responseCodeEnum  返回的响应码所对应的枚举类
     * @description         请求失败返回对象
     */
    public final ResponseResultVO successEnum(ResponseCodeEnum responseCodeEnum) {
        return new ResponseResultVO(responseCodeEnum.getCode(), responseCodeEnum.getMessage(), null);
    }

    /**
     * @param code      响应码
     * @param message   相应信息
     * @description     请求失败返回对象
     */
    public final ResponseResultVO error(int code, String message) {
        return new ResponseResultVO(code, message, null);
    }

}
