package com.neusoft.study.user.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.study.user.condition.UserCondition;
import com.neusoft.study.user.entity.User;
import com.neusoft.study.user.entity.UserAndExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author duanml
 * @since 2019-07-07
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过自定义SQL 查询所有满足条件的User对象
     * @param wrapper
     * @return
     */
    List<User> selectAll(@Param(Constants.WRAPPER) Wrapper<User> wrapper);

    /**
     * 多表关联查询
     * @param userCondition
     * @return
     */
    List<UserAndExtend> getPersionBase(UserCondition userCondition);

    /**
     * 多表关联查询分页实现
     * @param page
     * @param wrapper
     * @return
     */
    IPage<UserAndExtend> getUserPage(Page<UserAndExtend> page,@Param(Constants.WRAPPER) Wrapper<UserAndExtend> wrapper);
}
