package com.cosmos.friendsMatching.service;

import com.cosmos.friendsMatching.pojo.Team;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.pojo.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * <p>
 * 用户队伍关系表 服务类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-25
 */
public interface IUserTeamService extends IService<UserTeam> {


    /**
     * 加入队伍
     *
     * @param teamName 队伍名字
     * @param request  请求
     * @return 加入结果
     */
    boolean joinTeam(String teamName, HttpServletRequest request);

    /**
     * 根据用户id查询队伍
     *
     * @return 队伍列表
     */
    List<UserTeam> selectUserTeamByUserId(HttpServletRequest request);


    /**
     * 退出队伍
     *
     * @param teamName 队伍名字
     * @param request  请求
     * @return 退出结果
     */
    boolean exitTeam(String teamName, HttpServletRequest request);


    /**
     * 根据队伍id查询用户
     *
     * @param teamId 队伍id
     * @return 用户列表
     */
    List<User> selectUserByTeamId(Long teamId);


}
