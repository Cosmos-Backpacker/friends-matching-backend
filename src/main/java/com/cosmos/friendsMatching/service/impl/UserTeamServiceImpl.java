package com.cosmos.friendsMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.Team;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.pojo.UserTeam;
import com.cosmos.friendsMatching.mapper.UserTeamMapper;
import com.cosmos.friendsMatching.service.ITeamService;
import com.cosmos.friendsMatching.service.IUserService;
import com.cosmos.friendsMatching.service.IUserTeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cosmos.friendsMatching.utils.MySessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户队伍关系表 服务实现类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-25
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam> implements IUserTeamService {

    @Autowired
    private MySessionUtil mySessionUtil;

    @Autowired
    private ITeamService teamService;


    @Autowired
    private IUserService userService;


    @Override
    public boolean joinTeam(String teamName, HttpServletRequest request) {

        if (StringUtils.isBlank(teamName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");

        }
        long userId = mySessionUtil.getUserInfo(request).getId();
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getTeamName, teamName);

        Team team = teamService.getOne(wrapper);

        long teamId = team.getId();

        //检查队伍人数是否满
        int maxNum = team.getMaxNum();
        long userNum = teamService.count(wrapper);
        if (userNum >= maxNum) {
            throw new BusinessException(500, "加入失败", "人数已满");
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(LocalDateTime.now());
        userTeam.setCreateTime(LocalDateTime.now());
        userTeam.setUpdateTime(LocalDateTime.now());
        boolean result = this.save(userTeam);

        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");

        return true;

    }

    @Override
    public List<UserTeam> selectUserTeamByUserId(HttpServletRequest request) {

        long userId = mySessionUtil.getUserInfo(request).getId();

        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getUserId, userId);
        List<UserTeam> userTeamList = this.list(wrapper);

        if (userTeamList == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询失败");


        return userTeamList;
    }

    @Override
    public boolean exitTeam(String teamName, HttpServletRequest request) {

        if (StringUtils.isBlank(teamName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        long userId = mySessionUtil.getUserInfo(request).getId();


        //1.判断队伍是否存在
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getTeamName, teamName);
        Team team = teamService.getOne(wrapper);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }

        //2.判断是否在队伍中
        LambdaQueryWrapper<UserTeam> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(UserTeam::getUserId, userId);
        wrapper1.eq(UserTeam::getTeamId, team.getId());
        UserTeam userTeam = this.getOne(wrapper1);
        if (userTeam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "你不在队伍中");
        }


        //3.退出队伍
        boolean result = this.remove(wrapper1);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");

        return true;
    }

    @Override
    public List<User> selectUserByTeamId(Long teamId) {
        if (teamId == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");

        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getTeamId, teamId);
        List<UserTeam> userTeamList = this.list(wrapper);
        if (userTeamList == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询失败");
        if (userTeamList.isEmpty())
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍为空");

        log.error(userTeamList.toString());
        //获取userId
        List<Long> userIdList = userTeamList.stream().map(UserTeam::getUserId).toList();

        log.error(userIdList.toString());
        List<User> userList = userService.listByIds(userIdList).stream().map(user -> userService.getSafetyUser(user)).toList();

        log.error(userList.toString());
        //返回处理之后的安全的用户信息
        return userList;
    }
}
