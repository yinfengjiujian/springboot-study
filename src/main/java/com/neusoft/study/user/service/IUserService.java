package com.neusoft.study.user.service;

import com.neusoft.study.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.study.user.entity.UserInfo;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author duanml
 * @since 2019-07-07
 */
public interface IUserService extends IService<User> {

    UserInfo getUserInfo(String account);

    List<String> getUserRoles(Long userId);
}
