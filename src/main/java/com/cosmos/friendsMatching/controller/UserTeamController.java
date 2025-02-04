package com.cosmos.friendsMatching.controller;


import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.service.IUserTeamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author CosmosBackpacker
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/user-team")
@Slf4j
public class UserTeamController {


    @Autowired
    private IUserTeamService userTeamService;


    /**
     * 加入队伍
     *
     * @param teamName 队伍名字
     * @param request  请求
     * @return 是否加入成功
     */
    @PostMapping("/joinTeam")
    public Result joinTeam(String teamName, HttpServletRequest request) {
        if (StringUtils.isBlank(teamName))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");

        boolean result = userTeamService.joinTeam(teamName, request);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入失败");

        return Result.success("成功加入队伍");

    }


    /**
     * 根据用户id查询队伍
     *
     * @param request 请求
     * @return 队伍列表
     */
    @GetMapping("/selectUserTeamByUserId")
    public Result selectUserTeamByUserId(HttpServletRequest request) {
        return Result.success("查询成功", userTeamService.selectUserTeamByUserId(request));
    }


    /**
     * 退出队伍
     *
     * @param teamName 队伍名字
     * @param request  请求
     * @return 是否退出成功
     */
    @PostMapping("/exitTeam")
    public Result exitTeam(String teamName, HttpServletRequest request) {
        if (StringUtils.isBlank(teamName))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        boolean result = userTeamService.exitTeam(teamName, request);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出失败");

        return Result.success("成功退出队伍");
    }


    /**
     * 根据队伍id查询队伍成员
     *
     * @param teamId 队伍id
     * @return 队伍成员
     */
    @GetMapping("/selectTeamByUserId")
    public Result selectTeamByUserId(Long teamId) {
        log.info("teamId:{}", teamId);
        return Result.success("查询成功", userTeamService.selectUserByTeamId(teamId));
    }

}