package com.cosmos.friendsMatching.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.Tag;
import com.cosmos.friendsMatching.mapper.TagMapper;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cosmos.friendsMatching.utils.MySessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-01-15
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    @Autowired
    MySessionUtil mySessionUtil = new MySessionUtil();

    @Override
    public long createParentTag(String tagName, HttpServletRequest request) {
//      1. 判断tagName 是否为空
        if (tagName == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "tagName不能为空");

//        判断标签是否重复
        LambdaQueryWrapper<Tag> wrapper0 = new LambdaQueryWrapper<>();
        wrapper0.eq(Tag::getTagName, tagName);
        long count = this.count(wrapper0);
        if (count > 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签已存在");


//        2. 创建tag对象
        Tag tag = new Tag();
        tag.setTagName(tagName);
        User user = mySessionUtil.getUserInfo(request);
        tag.setUserId(user.getId());
        tag.setIsParentId(1);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());


//        3. 存储并返回结果
        boolean result = this.save(tag);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "标签创建失败");
        return tag.getId();
    }


    @Override
    public long createTag(String tagName, String parentTagName, HttpServletRequest request) {
        //1. 判断tagName 是否为空
        if (tagName == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "tagName不能为空");

        //2. 创建tag对象
        Tag tag = new Tag();
        tag.setTagName(tagName);
        User user = mySessionUtil.getUserInfo(request);
        tag.setUserId(user.getId());
        //判断子标签是否重复
        LambdaQueryWrapper<Tag> wrapper0 = new LambdaQueryWrapper<>();
        wrapper0.eq(Tag::getTagName, tagName);
        long count = this.count(wrapper0);
        if (count > 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签已存在");


        //3. 判断父标签是否存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getTagName, parentTagName);
        Tag parentTag = this.getOne(wrapper);
        if (parentTag == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父标签不存在");

        tag.setParentId(parentTag.getId());
        tag.setIsParentId(0);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());


        //3. 存储并返回结果
        boolean result = this.save(tag);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "标签创建失败");
        return tag.getId();
    }

    @Override
    public boolean deleteTagByTagName(String tagName, HttpServletRequest request) {
        //1. 判断tagName 是否为空
        if (tagName == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "tagName不能为空");
//       2. 判断删除者是用户还是管理员，用户只允许删除自己创建的标签，管理员可以删除任意标签
        long userId = mySessionUtil.getUserInfo(request).getId();

        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getTagName, tagName);
        Tag tag = this.getOne(wrapper);
        if (tag == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "标签不存在");
        }

        long tagUserId = tag.getUserId();

        if (!(mySessionUtil.isAdmin(request) || (tagUserId == userId))) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有权限操作");
        }


        //3.直接删除
        boolean result = this.removeById(tag);

        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");

        return true;
    }

    @Override
    public List<Tag> selectAllTag() {

        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        List<Tag> tagList = this.list(wrapper);
        Optional.ofNullable(tagList).orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_ERROR, "标签不存在"));
        return tagList;
    }

    @Override
    public Tag selectTagByTagName(String tagName) {
        Optional.ofNullable(tagName).orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "tagName不能为空"));
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getTagName, tagName);
        Tag tag = this.getOne(wrapper);
        Optional.ofNullable(tag).orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_ERROR, "标签不存在"));
        return tag;
    }

    @Override
    public List<Tag> selectAllParentTag() {

        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.eq("isParentId", 1);

        List<Tag> tagList = this.list(wrapper);
        Optional.ofNullable(tagList).orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_ERROR, "标签不存在"));
        return tagList;

    }

    @Override
    public List<Tag> selectTagByParentName(String parentTagName) {
        Optional.ofNullable(parentTagName).orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "parentTagName不能为空"));

        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.eq("isParentId", 0);
        wrapper.eq("isParentId", parentTagName);
        List<Tag> tagList = this.list(wrapper);
        Optional.ofNullable(tagList).orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_ERROR, "标签不存在"));
        return tagList;

    }



    @Override
    public List<Tag> selectChildTags() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getIsParentId, 0);
        this.list(wrapper);
        return this.list(wrapper);
    }


}
