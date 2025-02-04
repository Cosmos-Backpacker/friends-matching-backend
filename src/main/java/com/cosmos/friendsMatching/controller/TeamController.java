package com.cosmos.friendsMatching.controller;


import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.pojo.dto.TeamQuery;
import com.cosmos.friendsMatching.service.ITeamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author CosmosBackpacker
 * @since 2025-01-24
 */


@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Autowired
    private ITeamService teamService;

    /**
     * 创建队伍
     *
     * @param teamQuery 队伍信息
     * @param request   请求
     * @return 队伍id
     */
    @PostMapping("/createTeam")
    public Result addTeam(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入数据不能为空");
        }
        long teamId = teamService.createTeam(teamQuery, request);

        return Result.success("创建成功", teamId);
    }


    //删

    /**
     * 删除队伍
     *
     * @param teamName 队伍名字
     * @param request  请求
     * @return 是否删除成功
     */
    @PostMapping("/deleteTeam")
    public Result deleteTeam(@RequestParam String teamName, HttpServletRequest request) {
        if (StringUtils.isBlank(teamName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        if (teamService.deleteTeam(teamName, request))
            return Result.success("删除成功");
        return Result.error("删除失败");

    }


    //修改

    /**
     * 修改队伍信息
     *
     * @param teamQuery 队伍信息
     * @param request   请求
     * @return 是否修改成功
     */
    @PostMapping("/updateTeam")
    public Result updateTeam(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        boolean result = teamService.updateTeamInfo(teamQuery, request);

        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败");
        }

        return Result.success("修改成功");
    }


    //统一查询所有符合要求的队伍信息

    /**
     * 查询所有队伍信息
     *
     * @return 队伍信息
     */
    @PostMapping("/list")
    public Result teamList() {

        return Result.success("查询成功", teamService.teamList());

    }


    //分页查询组队信息

    /**
     * 分页查询队伍信息
     *
     * @param teamQuery 队伍信息
     * @return 队伍信息
     */
    @GetMapping("/findTeam")
    public Result findTeam(@RequestBody TeamQuery teamQuery) {
        //1.检查参数是否为空
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        return Result.success("查询成功", teamService.findTeam(teamQuery));
    }


    /**
     * 根据队伍id查询队伍信息
     *
     * @param id 队伍id
     * @return 队伍信息
     */
    @GetMapping("/findTeamById")
    public Result findTeamById(long id) {
        if (id <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        return Result.success("查询成功", teamService.getById(id));
    }


}
