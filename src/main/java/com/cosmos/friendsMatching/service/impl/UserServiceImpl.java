package com.cosmos.friendsMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.mapper.UserMapper;
import com.cosmos.friendsMatching.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cosmos.friendsMatching.utils.CosineSimilarity;
import com.cosmos.friendsMatching.utils.MySessionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2024-11-25
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private MySessionUtil mySessionUtil;


    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;
    //盐值 用于混淆加密
    private static final String SALT = "cosmos";
    /**
     * 用户登录态的键
     */
    public static final String USER_LOGIN_STATE = "userLoginState";

    @Override
    public Result userRegister(String account, String password, String checkPassword) {
        //1.校验是否为空
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能为空");
        }

        if (account.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能为空");
        }

        if (password.length() < 8 || checkPassword.length() < 8) {
            return Result.error("密码不能小于8位");

        }


        //2.检验是否包含特殊字符

        //匹配字符差串中是否包含特殊字符
        String validPattern = "[^a-zA-Z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(account);
        if (matcher.find()) {
            return Result.error("不能包含特殊字符");
        }

        //3.检验密码是否相同
        if (!password.equals(checkPassword)) {
            return Result.error("密码不相同");
        }


        //4.检验是否存在相同账号的成员，这里可以直接简要的写一个查询不必封装
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUserAccount, account);

        long count = 0;
        //直接查找数目，不用返回数据了
        count = mapper.selectCount(wrapper);
        if (count > 0) {
            return Result.error("已有账户存在");
        }

        //5.加密密码
        //用springboot自带的方法进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        //6.插入数据
        User newUser = new User();
        newUser.setUserAccount(account);
        newUser.setUserPassword(encryptPassword);
        int result = mapper.insert(newUser);
        if (result <= 0)//代表操作失败
        {
            return Result.error("插入数据错误，注册失败");
        }
//MyBatis-Plus在执行插入操作后，会从数据库中获取自动生成的ID，并将其设置回插入的对象
        return Result.success("恭喜你注册成功！！", newUser.getId());
    }

    @Override
    public Result userLogin(String account, String password, HttpServletRequest request) {

        //1.校验是否为空
        if (StringUtils.isAnyBlank(account, password)) {
            log.info(account, password);
            return Result.error("不能为空");
        }

        //2.检验账号密码长度
        if (account.length() < 4) {
            return Result.error("账号不能小于4位");
        }

        if (password.length() < 8) {
            return Result.error("密码不能小于8位");

        }
        //3.匹配字符差串中是否包含特殊字符
        String validPattern = "[^a-zA-Z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(account);
        if (matcher.find()) {
            return Result.error("不能包含特殊字符");
        }

        //4.加密密码
        //用springboot自带的方法进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        //5.查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUserAccount, account)
                .eq(User::getUserPassword, encryptPassword);

        User user;
        user = mapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        } else {

            //往session中设置
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            //脱敏
            //这里创建一个新的用户对象用于返回部分值到前端
            User safetyUser = getSafetyUser(user);
            return Result.success("恭喜你登录成功！！", safetyUser);
        }

    }


    public User getSafetyUser(User origenUser) {
        if (origenUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(origenUser.getId());
        safetyUser.setUsername(origenUser.getUsername());
        safetyUser.setAvatarUrl(origenUser.getAvatarUrl());
        safetyUser.setGender(origenUser.getGender());
        safetyUser.setPhone(origenUser.getPhone());
        safetyUser.setRole(origenUser.getRole());
        safetyUser.setEmail(origenUser.getEmail());
        safetyUser.setTags(origenUser.getTags());
        return safetyUser;
    }

    @Override
    public Result userLayout(HttpServletRequest request) {

        if (request.getSession().getAttribute(USER_LOGIN_STATE) != null) {
            request.getSession().removeAttribute(USER_LOGIN_STATE);
            return Result.success("账号已退出");
        }
        //移除登录态

        throw new BusinessException(500, "请先登录", "");
    }

    @Override
    public boolean updateUserInfo(User user, HttpServletRequest request) {

        //1.获取当前登录用户
        User loginUser = mySessionUtil.getUserInfo(request);
        //2.获取用户id
        Long userId = loginUser.getId();

        boolean result = this.updateById(user);

        return result;
    }


    @Override
    public List<User> selectUserByTags(List<String> tagNameList, int pageSize, int pageNum) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");

        }
        //通过内存查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        Gson gson = new Gson();

        Page<User> page = new Page<>(pageSize, pageNum);
        //查询出所有用户
        Page<User> userPage = mapper.selectPage(page, wrapper);

        //获取用户列表
        List<User> userList = userPage.getRecords();

        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            Set<String> tempTagSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());

            //二次检验，确保set不为null,如果为null，返回一个空的set
            tempTagSet = Optional.ofNullable(tempTagSet).orElse(new HashSet<String>());

            for (String tagName : tagNameList) {
                if (!tempTagSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }


    //@Deprecated代表是过时了的方法
    @Deprecated
    @Override
    public List<User> selectUserByTagsBySql(List<String> tagNameList, int current, int pageNum) {

        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");

        }
        //根据数据库进行查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();

        Page<User> page = new Page<>(current, pageNum);
        //通过循环like来实现
        for (String tagName : tagNameList) {
            wrapper = wrapper.like(User::getTags, tagName);
        }
        List<User> userList = mapper.selectList(page, wrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

    }

    @Override
    public Page<User> recommendUser(Long pageNum, Long pageSize, HttpServletRequest request) {
        //1.获取登录用户
        User userObj = mySessionUtil.getUserInfo(request);

        //2.获取redis的key
        ValueOperations valueOperations = redisTemplate.opsForValue();


        if (userObj == null) {
            String redisUsualKey = String.format("cosmos:user:recommend:usual");
            Page<User> userPageUsual = (Page<User>) valueOperations.get(redisUsualKey);
            if (userPageUsual != null)
                return userPageUsual;

            //否则通用查询一下
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            userPageUsual = this.page(new Page<>(pageNum, pageSize), queryWrapper);
            //写入缓存中,时间为1天
            try {
                valueOperations.set(redisUsualKey, userPageUsual, 1, TimeUnit.DAYS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }

            return userPageUsual;


        } else {

            //定义根据用户id查询
            String redisKey = String.format("cosmos:user:recommend:%s", userObj.getId());
            Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
            if (userPage != null) {
                return userPage;
            }


            //todo 根据推荐算法实现精确的推荐
            //获取当前用户的标签
            String userTags = userObj.getTags();
            Gson gson = new Gson();
            Set<String> userTagSet = gson.fromJson(userTags, new TypeToken<Set<String>>() {
            }.getType());

            //二次检验，确保set不为null,如果为null，返回一个空的set
            userTagSet = Optional.ofNullable(userTagSet).orElse(new HashSet<String>());

            //如果没有缓存，从数据库进行读取后写入缓存
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();

            userPage = this.page(new Page<>(pageNum, 100), queryWrapper);


// 定义一个优先队列，按照相似度从大到小排序
            PriorityQueue<Map.Entry<Double, User>> priorityQueue = new PriorityQueue<>(
                    (entry1, entry2) -> Double.compare(entry2.getKey(), entry1.getKey())
            );

// 遍历所有用户
            for (User user : userPage.getRecords()) {
                // 获取用户的标签
                String tags = user.getTags();
                Set<String> tagsSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
                }.getType());

                // 二次检验，确保set不为null,如果为null，返回一个空的set
                tagsSet = Optional.ofNullable(tagsSet).orElse(new HashSet<>());

                // 计算相似度
                double similarity = CosineSimilarity.cosine(userTagSet, tagsSet);

                // 放入优先队列中
                priorityQueue.offer(new AbstractMap.SimpleEntry<>(similarity, user));
            }

// 取出前pageSize个数据
            List<User> userLists = new ArrayList<>();
            for (int i = 0; i < pageSize && !priorityQueue.isEmpty(); i++) {
                Map.Entry<Double, User> entry = priorityQueue.poll();
                log.info("similarity:{}", entry.getKey().toString());
                userLists.add(getSafetyUser(entry.getValue()));
            }

            System.out.println(userLists.toString());

            //将userList放入userPage中
            userPage.setRecords(userLists);

            //写缓存
            try {
                //设置过期时间为1天
                valueOperations.set(redisKey, userPage, 1, TimeUnit.DAYS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }

            return userPage;
        }

    }

    @Override
    public User selectUserById(Long userId) {
        if (userId == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");

        User user = mapper.selectById(userId);
        return getSafetyUser(user);
    }


    @Override
    public List<User> selectAllByUsername(String username, HttpServletRequest request) {


        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "权限不足");
        }


        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.like(User::getUsername, username);


        List<User> userList = mapper.selectList(wrapper);
        //脱敏
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public Result deleteUserById(Integer id, HttpServletRequest request) {

        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "权限不足");
        }

        int affectRows = mapper.deleteById(id);
        if (affectRows > 0)
            return Result.success("删除成功");
        return Result.error("删除失败");
    }


    /**
     * 判断是否为管理员，将公共部分抽出来成为一个函数
     *
     * @param request 请求参数
     * @return 是否为管理员 是true 否false
     */
    private boolean isAdmin(HttpServletRequest request) {
        //从session中获取用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);

        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH, "未登录");
        }

        User user = (User) userObj;

        return user.getRole() == 1;
    }


}
