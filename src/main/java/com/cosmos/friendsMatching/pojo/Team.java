package com.cosmos.friendsMatching.pojo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 队伍表
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("team")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名
     */
    private String teamName;

    /**
     * 队长ID
     */
    private Long userId;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 密码
     */
    private String userPassword;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


    /**
     * 0未删除，1删除
     */
    @TableLogic
    private Integer isDelete;


}
