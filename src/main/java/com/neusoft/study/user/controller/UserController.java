package com.neusoft.study.user.controller;


import com.neusoft.study.common.exception.BusinessException;
import com.neusoft.study.common.response.ResponseCodeEnum;
import com.neusoft.study.common.response.ResponseResultUtil;
import com.neusoft.study.common.response.ResponseResultVO;
import com.neusoft.study.user.entity.User;
import com.neusoft.study.user.entity.UserInfo;
import com.neusoft.study.user.service.impl.UserJwtServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author duanml
 * @since 2019-07-07
 */
@Api(tags = "个人业务")
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserJwtServiceImpl userJwtService;

    @Autowired
    private ResponseResultUtil responseResultUtil;

    @RequestMapping(value="/user/getUser",method= RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "个人信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<ResponseResultVO> getUser() {
        User user = new User();
        user.setAccount("duanml");
        user.setAge(30);
        user.setPassword("4f5dafw6");
        return ResponseEntity.ok(responseResultUtil.success(user));
    }

    /**
     * 用户名密码登录
     * @param request
     * @return token
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<ResponseResultVO> login(@RequestBody UserInfo loginInfo, HttpServletRequest request, HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(loginInfo.getAccount(), loginInfo.getPassword());
            subject.login(token);
            UserInfo userInfo = (UserInfo) subject.getPrincipal();
            String newToken = userJwtService.generateJwtToken(userInfo.getAccount());
            response.setHeader("x-auth-token", newToken);
            return ResponseEntity.ok(responseResultUtil.successEnum(ResponseCodeEnum.LOGIN_SUCCESS));
        } catch (UnknownAccountException e) {
            log.error("There is no user with username of " + loginInfo.getAccount());
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND_USER);
        } catch (IncorrectCredentialsException e) {
            log.error("Password for account " + loginInfo.getPassword() + " was incorrect!");
            throw new BusinessException(ResponseCodeEnum.ERROR_PASSWORD);
        } catch (LockedAccountException e) {
            log.error("The account for username " + loginInfo.getAccount() + " is locked.  " +
                    "Please contact your administrator to unlock it.");
            throw new BusinessException(ResponseCodeEnum.LOCKED_USER);
        } catch (BusinessException e) {
            log.error("User {} login fail, Reason:{}", loginInfo.getAccount(), e.getMessage());
            throw new BusinessException(ResponseCodeEnum.LOGIN_FAILED);
        } catch (AuthenticationException e) {
            log.error("User {} login fail, Reason:{}", loginInfo.getAccount(), e.getMessage());
            throw new BusinessException(ResponseCodeEnum.LOGIN_FAILED);
        } catch (Exception e) {
            throw new BusinessException(ResponseCodeEnum.LOGIN_FAILED);
        }
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping(value = "/logout")
    public ResponseEntity<ResponseResultVO> logout() {
        Subject subject = SecurityUtils.getSubject();
        if(subject.getPrincipals() != null) {
            UserInfo userInfo = (UserInfo) subject.getPrincipals().getPrimaryPrincipal();
            userJwtService.deleteLoginInfo(userInfo.getAccount());
        }
        SecurityUtils.getSubject().logout();
        return ResponseEntity.ok(responseResultUtil.success());
    }


}
