package com.cosmos.friendsMatching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2024-11-25
 */

@Service
public interface IUserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param account       用户账号
     * @param password      用户密码
     * @param checkPassword 校验密码
     * @return
     */

    Result userRegister(String account, String password, String checkPassword);


    /**
     * 用户登录
     *
     * @param account  登录账号
     * @param password 登录密码
     * @param request  请求参数，存储session
     * @return
     */
    Result userLogin(String account, String password, HttpServletRequest request);


    /**
     * 用户查询
     *
     * @param username 用户名
     */
    List<User> selectAllByUsername(String username, HttpServletRequest request);


    /**
     * 删除用户
     */
    Result deleteUserById(Integer id, HttpServletRequest request);


    /**
     * 抽出一个方法，给用户信息脱敏
     *
     * @param origenUser 原始用户数据
     * @return 脱敏后的用户数据
     */

    public User getSafetyUser(User origenUser);


    /**
     * 用户注销
     *
     * @param request 请求参数
     * @return 注销结果
     */
    public Result userLayout(HttpServletRequest request);


    /**
     * 更改用户信息
     *
     * @param user    用户信息
     * @param request 请求参数
     * @return 更改结果
     */
    public boolean updateUserInfo(User user, HttpServletRequest request);


    /**
     * 根据标签分页查询用户信息
     *
     * @param tagNameList 标签列表
     * @param pageSize    每页大小
     * @param pageNum     当前页码
     * @return 用户列表
     */
    public List<User> selectUserByTags(List<String> tagNameList, int pageSize, int pageNum);

    //@Deprecated代表是过时了的方法

    /**
     * 根据标签分页查询用户信息
     *
     * @param tagNameList 标签列表
     * @param current     当前页码
     * @param pageNum     当前页码
     * @return 用户列表
     */
    @Deprecated
    List<User> selectUserByTagsBySql(List<String> tagNameList, int current, int pageNum);


    public Page<User> recommendUser(Long pageNum, Long pageSize, HttpServletRequest request);

    /**
     * 根据id查询用户
     *
     * @param userId 用户id
     * @return 用户信息
     */
    public User selectUserById(Long userId);


}
