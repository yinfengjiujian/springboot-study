package com.neusoft.study.user.controller;


import com.neusoft.study.user.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value="/getUser",method= RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "个人信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody User getUser() {
        User user = new User();
        user.setAccount("duanml");
        user.setAge(30);
        user.setPassword("4f5dafw6");
        return user;
    }

}
