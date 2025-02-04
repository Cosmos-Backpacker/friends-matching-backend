package com.cosmos.friendsMatching.controller;

import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.pojo.Tag;
import com.cosmos.friendsMatching.service.ITagService;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Frequency;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author CosmosBackpacker
 * @since 2025-01-15
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private ITagService tagService;


    /**
     * 创建父标签(分类)
     *
     * @param tagName 标签名
     * @param request 请求
     * @return 标签id
     */

    @PostMapping("/createParentTag")
    public Result createParentTag(String tagName, HttpServletRequest request) {
        if (tagName == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名不能为空");
        }
        long tagId = tagService.createParentTag(tagName, request);
        return Result.success("创建分类成功", tagId);
    }


    /**
     * 创建子标签
     *
     * @param tagName 队伍
     * @param request 请求
     * @return 标签id
     */
    @PostMapping("/createTag")
    public Result createTag(String tagName, String parentTagName, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(tagName, parentTagName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名，父标签名不能为空");
        }
        long tagId = tagService.createTag(tagName, parentTagName, request);
        return Result.success("创建成功", tagId);
    }


    /**
     * 删除标签
     *
     * @param tagName 标签
     * @param request 请求
     * @return 是否删除成功
     */
    @DeleteMapping("/deleteTagByTagName")
    public Result deleteTagByTagName(String tagName, HttpServletRequest request) {
        if (StringUtils.isBlank(tagName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名不能为空");
        }
        boolean result = tagService.deleteTagByTagName(tagName, request);
        return Result.success("删除成功");

    }


    /**
     * 查询所有标签
     *
     * @return 标签列表
     */

    @GetMapping("/selectAllTag")
    public Result selectAllTag() {
        List<Tag> tagList = tagService.selectAllTag();
        return Result.success("查询成功", tagList);
    }

    /**
     * 根据标签名查询标签
     *
     * @return 标签
     */
    @GetMapping("/selectTagByTagName")
    public Result selectTagByTagName(String tagName) {
        if (StringUtils.isBlank(tagName))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名不能为空");
        Tag tag = tagService.selectTagByTagName(tagName);
        return Result.success("查询成功", tag);

    }


    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    @GetMapping("/selectAllParentTag")
    public Result selectAllParentTag() {
        List<Tag> tagList = tagService.selectAllParentTag();
        return Result.success("分类查询成功", tagList);
    }


    /**
     * 根据父标签(分类)查询子标签
     *
     * @param parentTagName 父标签
     * @return 子标签列表
     */
    @GetMapping("/selectTagByParentName")
    public Result selectTagByParentName(String parentTagName) {
        if (StringUtils.isBlank(parentTagName))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父标签名不能为空");

        List<Tag> tagList = tagService.selectTagByParentName(parentTagName);
        return Result.success("查询成功", tagList);
    }

    /**
     * 查询子标签
     *
     * @return 子标签列表
     */
    @GetMapping("/selectChildTag")
    public Result selectChildTag() {
        return Result.success("查询成功", tagService.selectChildTags());
    }


}
