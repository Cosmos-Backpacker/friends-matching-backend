package com.cosmos.friendsMatching.controller;


import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping
@Slf4j
public class loginController {
    @Autowired
    IUserService userService;

    @PostMapping("/login")
    public Result login(@RequestParam String userAccount, @RequestParam String userPassword, HttpServletRequest request) {
        log.error(userAccount, userPassword);
        return userService.userLogin(userAccount, userPassword, request);

    }

}
