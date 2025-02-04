package com.cosmos.friendsMatching.controller;


import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class registerController {

    @Autowired
    IUserService userService;

    /**
     *用户注册
     * @param account   用户账户
     * @param password  用户密码
     * @param checkPassword 校验密码
     * @return 返回通用结果
     */

    @PostMapping("/register")
    public Result register(String account, String password, String checkPassword) {

        return userService.userRegister(account, password, checkPassword);

    }

}
