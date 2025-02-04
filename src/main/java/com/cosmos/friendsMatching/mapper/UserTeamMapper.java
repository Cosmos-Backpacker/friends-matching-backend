package com.cosmos.friendsMatching.mapper;

import com.cosmos.friendsMatching.pojo.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户队伍关系表 Mapper 接口
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-25
 */
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}
