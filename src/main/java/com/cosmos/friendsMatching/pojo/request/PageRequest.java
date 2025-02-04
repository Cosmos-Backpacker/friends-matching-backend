package com.cosmos.friendsMatching.pojo.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页面
     */
    protected int pageSize = 1;

    /**
     * 每一页的个数
     */
    protected int pageNum = 5;

}
