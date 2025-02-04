package com.cosmos.friendsMatching.service;

import com.cosmos.friendsMatching.pojo.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cosmos.friendsMatching.pojo.Team;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 标签 服务类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-15
 */
@Service
public interface ITagService extends IService<Tag> {

    /**
     * 创建父标签(分类)
     *
     * @param tagName 标签名
     * @param request 请求
     * @return 标签id
     */
    public long createParentTag(String tagName, HttpServletRequest request);


    /**
     * 创建子标签
     *
     * @param tagName 队伍
     * @param request 请求
     * @return 标签id
     */
    public long createTag(String tagName, String parentTagName, HttpServletRequest request);


    /**
     * 删除标签
     *
     * @param tagName 标签
     * @param request 请求
     * @return 是否删除成功
     */
    public boolean deleteTagByTagName(String tagName, HttpServletRequest request);


    /**
     *  查询所有标签
     * @return  标签列表
     */
    public List<Tag> selectAllTag();

    /**
     *  根据标签名查询标签
     * @return  标签
     */
    public Tag selectTagByTagName(String tagName);


    /**
     *  查询所有分类
     * @return  分类列表
     */
    public List<Tag> selectAllParentTag();


    /**
     *  根据父标签(分类)查询子标签
     * @param parentTagName  父标签
     * @return  子标签列表
     */
    public List<Tag> selectTagByParentName(String parentTagName);


    /**
     *  获取所有子标签
     * @return  子标签列表
     */
    public List<Tag> selectChildTags();



}
