package com.neusoft.study.entity.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: com.neusoft.study.entity.user</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/30 0030 11:00
 * Description: No Description
 */
@Data
public class UserDto implements Serializable {
    private String username;
    private char[] password;
    private String encryptPwd;
    private Long userId;
    private String salt;
    private List<String> roles;
}
