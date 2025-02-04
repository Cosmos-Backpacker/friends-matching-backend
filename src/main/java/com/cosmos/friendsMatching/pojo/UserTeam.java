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
 * 用户队伍关系表
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_team")
public class UserTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 队伍ID
     */
    private Long teamId;

    /**
     * 用户加入时间
     */
    private LocalDateTime joinTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 0未删除，1删除
     */
    @TableLogic
    private Integer isDelete;

}
