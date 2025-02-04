package com.cosmos.friendsMatching.service;

import com.cosmos.friendsMatching.pojo.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.pojo.dto.TeamQuery;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 队伍表 服务类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-24
 */


@Service
public interface ITeamService extends IService<Team> {


    /**
     * 创建队伍
     *
     * @param teamQuery 请求对象
     * @param request   请求
     * @return 队伍id
     * @author CosmosBackpacker
     */
    public long createTeam(TeamQuery teamQuery, HttpServletRequest request);

    /**
     * 删除队伍
     *
     * @param teamName 队伍名
     * @param request  请求
     * @return 是否成功
     * @author CosmosBackpacker
     */
    public boolean deleteTeam(String teamName, HttpServletRequest request);


    /**
     * 修改队伍信息
     *
     * @param teamQuery 请求对象
     * @param request   请求
     * @return 是否成功
     * @author CosmosBackpacker
     */
    public boolean updateTeamInfo(TeamQuery teamQuery, HttpServletRequest request);


    /**
     * 获取队伍列表
     *
     * @return 队伍列表
     * @author CosmosBackpacker
     */
    public List<Team> teamList();

    /**
     * 获取队伍详情
     *
     * @param teamQuery 请求对象
     * @return 队伍详情
     * @author CosmosBackpacker
     */
    public List<Team> findTeam(TeamQuery teamQuery);


    /**
     *  获取队伍详情
     * @param teamId     队伍id
     * @return          队伍详情
     */
    public Team findTeamById(long teamId);


}
