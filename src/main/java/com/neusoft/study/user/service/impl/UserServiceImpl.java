package com.neusoft.study.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.study.common.exception.BusinessException;
import com.neusoft.study.common.response.ResponseCodeEnum;
import com.neusoft.study.user.dao.UserMapper;
import com.neusoft.study.user.entity.User;
import com.neusoft.study.user.entity.UserInfo;
import com.neusoft.study.user.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

    /**
     * 获取用户账户查询数据库中保存的用户信息
     *
     * @param account
     * @return
     */
    public UserInfo getUserInfo(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("account",account);
        List<User> users = baseMapper.selectList(queryWrapper);
        if (users.size() > 1){
            throw new BusinessException(ResponseCodeEnum.TWO_USERS);
        }
        if (users.size() <= 0){
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND_USER);
        }
        User user = users.get(0);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserid());
        userInfo.setAccount(user.getAccount());
        userInfo.setUsername(user.getUsername());
        userInfo.setEncryptPwd(user.getPassword());
        userInfo.setPasswrdSalt(user.getUserSalt());
        return userInfo;
    }

    /**
     * 获取用户角色列表，强烈建议从缓存中获取
     *
     * @param userId
     * @return
     */
    public List<String> getUserRoles(Long userId) {
        return Arrays.asList("admin");
    }

}
