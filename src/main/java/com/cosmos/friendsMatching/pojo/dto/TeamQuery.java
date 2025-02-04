package com.cosmos.friendsMatching.pojo.dto;

import com.cosmos.friendsMatching.pojo.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamQuery extends PageRequest {

    /**
     * 队伍ID
     */
    private Long id;

    /**
     * 队伍名  *
     */
    private String teamName;

    /**
     * 队长ID
     */
    private Long userId;

    /**
     * 队伍描述     Null
     */
    private String description;

    /**
     * 最大人数     *
     */
    private Integer maxNum;

    /**
     * 0-公开，1-加密   null 默认0
     */
    private Integer status;

    /**
     * 过期时间     *
     */
    private LocalDateTime expireTime;

    /**
     * 密码   null
     */
    private String userPassword;


}
