package com.neusoft.study.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户扩展信息表
 * </p>
 *
 * @author duanml
 * @since 2019-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_extend")
public class UserExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "userid", type = IdType.ID_WORKER)
    private Long userid;

    /**
     * 用户头像
     */
    @TableField("userimage")
    private String userimage;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;


}
