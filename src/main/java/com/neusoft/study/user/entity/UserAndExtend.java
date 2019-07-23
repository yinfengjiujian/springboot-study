package com.neusoft.study.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>Title: com.neusoft.study.user.entity</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/13 0013 20:30
 * Description: No Description
 */
@Data
public class UserAndExtend implements Serializable {

    /**
     * 用户id
     */
    @TableField("userid")
    private Long userid;

    /**
     * 用户账户
     */
    @TableField("account")
    private String account;

    /**
     * 用户姓名
     */
    @TableField("username")
    private String username;

    /**
     * 用户密码
     */
    @TableField("password")
    private String password;

    /**
     * 性别
     */
    @TableField("sex")
    private String sex;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("usermail")
    private String usermail;

    /**
     * 是否锁定
     */
    @TableField("islock")
    private String islock;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private LocalDateTime createDate;

    /**
     * 年龄
     */
    @TableField("age")
    private Integer age;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;
}
