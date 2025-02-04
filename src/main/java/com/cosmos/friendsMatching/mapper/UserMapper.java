package com.cosmos.friendsMatching.mapper;

import com.cosmos.friendsMatching.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2024-11-25
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
