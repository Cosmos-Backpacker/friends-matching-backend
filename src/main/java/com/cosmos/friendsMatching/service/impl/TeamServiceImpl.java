package com.cosmos.friendsMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.*;
import com.cosmos.friendsMatching.mapper.TeamMapper;
import com.cosmos.friendsMatching.pojo.dto.TeamQuery;
import com.cosmos.friendsMatching.service.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cosmos.friendsMatching.service.IUserTeamService;
import com.cosmos.friendsMatching.utils.MySessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * <p>
 * 队伍表 服务实现类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-24
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {


    @Autowired
    private MySessionUtil mySessionUtil;


    @Autowired
    @Lazy
    private IUserTeamService userTeamService;


    //当出现BusinessException时，事务回滚
    @Transactional(rollbackFor = BusinessException.class)
    @Override
    public long createTeam(TeamQuery teamQuery, HttpServletRequest request) {

        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入数据不能为空");
        }


        //获取当前用户身份
        User user = mySessionUtil.getUserInfo(request);
        //一个用户最多创建3个队伍
        LambdaQueryWrapper<Team> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Team::getUserId, user.getId());
        long count = this.count(wrapper1);
        if (count >= 3)
            throw new BusinessException(ErrorCode.NO_AUTH, "您已经创建了3个队伍");

        //对teamQuery进行校验
        if (StringUtils.isBlank(teamQuery.getTeamName()))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名不能为空");

        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getTeamName, teamQuery.getTeamName());
        Team team1 = this.getOne(wrapper);
        if (team1 != null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已存在");


        if (teamQuery.getMaxNum() == null || teamQuery.getMaxNum() <= 0 || teamQuery.getMaxNum() > 10)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不能为空");


        if (teamQuery.getStatus().equals(1))    //如果设置加密房间必须设置密码
        {
            if (StringUtils.isBlank(teamQuery.getUserPassword()))
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须设置密码");
        } else if (teamQuery.getStatus().equals(0))
            teamQuery.setUserPassword(null);
        else
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态错误");


        //对过期时间进行校验
        LocalDateTime expireTime = Optional.ofNullable(teamQuery.getExpireTime())
                .orElse(LocalDateTime.now().plusDays(1));
        //如果为空就默认加一天到期
        teamQuery.setExpireTime(expireTime);


        //将teamQuery对象转换成Team对象
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);

        //完善team信息
        team.setUserId(user.getId());
        team.setCreateTime(LocalDateTime.now());
        team.setUpdateTime(LocalDateTime.now());


        boolean resultTeam = this.save(team);

        //封装用户队伍表信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(user.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(LocalDateTime.now());
        userTeam.setCreateTime(LocalDateTime.now());
        userTeam.setUpdateTime(LocalDateTime.now());


        //保存信息
        boolean resultUserTeam = userTeamService.save(userTeam);


        if (!(resultTeam && resultUserTeam)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建失败");
        }

        return team.getId();
    }


    @Transactional(rollbackFor = BusinessException.class)
    @Override
    public boolean deleteTeam(String teamName, HttpServletRequest request) {
        //1.判空
        if (StringUtils.isBlank(teamName))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名不能为空");

        //2.验证身份
        //获取当前用户身份
        long userId = mySessionUtil.getUserInfo(request).getId();
        //获取队伍队长身份
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getTeamName, teamName);
        Team teamDel = this.getOne(wrapper);
        Optional.ofNullable(teamDel).orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "该队伍不存在"));

        if (!(mySessionUtil.isAdmin(request) || (userId == teamDel.getUserId()))) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有权限删除");
        }


        //删除队伍
        boolean result = this.removeById(teamDel);

        //同时删除这个队伍中所有的用户
        LambdaQueryWrapper<UserTeam> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(UserTeam::getTeamId, teamDel.getId());
        boolean result1 = userTeamService.remove(wrapper1);
        if (!(result && result1))
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");

        return true;
    }

    @Override
    public boolean updateTeamInfo(TeamQuery teamQuery, HttpServletRequest request) {

        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        //2.只能由队长或者管理员修改
        User user = mySessionUtil.getUserInfo(request);

        if (!(mySessionUtil.isAdmin(request) || user.getId().equals(teamQuery.getUserId()))) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有权限删除");
        }


        //3.转换修改对象
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        team.setUpdateTime(LocalDateTime.now());


        //4.执行修改
        boolean result = this.updateById(team);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败");

        return true;
    }


    @Override
    public List<Team> teamList() {

        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();


        return this.list(wrapper);
    }

    @Override
    public List<Team> findTeam(TeamQuery teamQuery) {

        //1.检查参数是否为空
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>(team);

        //创建page对象
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());

        Page<Team> pageList = this.page(page, wrapper);

        return pageList.getRecords();
    }



    @Override
    public Team findTeamById(long teamId) {

        if (teamId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        Team team = this.getById(teamId);
        if (team == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        return team;
    }


}
