package com.neusoft.study.user.service.impl;

import com.neusoft.study.user.entity.User;
import com.neusoft.study.user.dao.UserMapper;
import com.neusoft.study.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author duanml
 * @since 2019-07-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
