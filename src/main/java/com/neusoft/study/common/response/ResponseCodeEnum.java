package com.neusoft.study.common.response;

/**
 * <p>Title: com.neusoft.study.common.exception</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/27 0027 20:11
 * Description: No Description
 */
public enum ResponseCodeEnum {

    // 系统通用
    SUCCESS(200, "操作成功"),

    UNLOGIN_ERROR(233, "没有登录"),

    LOGIN_SUCCESS(200, "登录成功！"),

    LOGIN_FAILED(200, "登录失败！账号和密码不对"),

    OPERATE_FAIL(666, "操作失败"),

    TOKEN_EXPIRED(203,"token已经过期，请重新登录！"),

    TOKEN_ERROR(203,"无效的token，请重新登录！"),

    NOT_FOUND_TOKEN(203,"非法的请求，无token，请先登录！"),

    FAILD_ACTION(500,"发生系统错误，请联系管理员！"),

    // 用户
    TWO_USERS(300, "存在多个用户信息，请联系管理员"),
    NOT_FOUND_USER(300, "根据用户账号没有查询到相关用户信息！"),
    ERROR_PASSWORD(300, "密码错误，登录失败！"),
    LOCKED_USER(300, "账号被锁定，无法登录！"),

    SAVE_USER_INFO_FAILED(2001, "保存用户信息失败"),

    GET_USER_INFO_FAILED(2002, "保存用户信息失败"),

    WECHAT_VALID_FAILED(2003, "微信验证失败"),

    GET_USER_AUTH_INFO_FAILED(2004, "根据条件获取用户授权信息失败"),

    SAVE_USER_AUTH_INFO_FAILED(2005, "保存用户授权失败");

    private Integer code;
    private String message;

    ResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public final Integer getCode() {
        return this.code;
    }

    public final String getMessage() {
        return this.message;
    }


}
