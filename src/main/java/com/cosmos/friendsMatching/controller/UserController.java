package com.cosmos.friendsMatching.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.service.IUserService;
import com.cosmos.friendsMatching.utils.CosineSimilarity;
import com.cosmos.friendsMatching.utils.MySessionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cosmos.friendsMatching.service.impl.UserServiceImpl.USER_LOGIN_STATE;


/**
 * @author CosmosBackpacker
 * @since 2024-11-25
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;


    @GetMapping("/selectUserByUsername")
    public Result selectAllUser(@RequestParam String username, HttpServletRequest request) {

        List<User> userList = userService.selectAllByUsername(username, request);

        if (userList != null) {
            return Result.success("查询成功", userList);
        }
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }


    @DeleteMapping("/delete")
    public Result deleteUserById(Integer id, HttpServletRequest request) {
        return userService.deleteUserById(id, request);
    }


    @GetMapping("/userLayout")
    public Result userLayout(HttpServletRequest request) {
        return userService.userLayout(request);
    }


    @GetMapping("/selectByTags")
    public Result selectUserListByTags(@RequestParam(required = false) List<String> tagNameList,
                                       @RequestParam(defaultValue = "1") Integer pageSize,
                                       @RequestParam(defaultValue = "20") Integer pageNum) {

        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }


        return Result.success("success", userService.selectUserByTags(tagNameList, pageSize, pageNum));
    }


    /**
     * @param pageNum  当前页数
     * @param pageSize 每一页的条数
     * @param request  请求
     * @return 分页列表
     */
    //改造成先查询数据然后将数据内存中
    @GetMapping("/recommend")
    public Result recommendUser(Long pageNum, Long pageSize, HttpServletRequest request) {
        // 1. 判断参数
        if (pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        return Result.success("成功", userService.recommendUser(pageNum, pageSize, request));
    }


    @PostMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        log.error(user.toString());
        boolean result = userService.updateUserInfo(user, request);

        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "信息更新失败");


        //根据实际需求前端更新完成之后需要重新获取更新后的对象
        User newUser = userService.getById(user.getId());

        //返回安全数据
        return Result.success("信息更新成功", userService.getSafetyUser(newUser));
    }


    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    public Result getUserInfo(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        User user = userService.selectUserById(userId);
        return Result.success("success", user);
    }


}
