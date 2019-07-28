package com.neusoft.study.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: com.neusoft.study.user.entity</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/23 0023 22:14
 * Description: No Description
 */
@Data
public class UserInfo implements Serializable {

    private String account;
    private String username;
    private String password;
    private String encryptPwd;
    private Long userId;
    private String tokenSalt;
    private String passwrdSalt;
    private List<String> roles;
}
