package com.cosmos.friendsMatching.utils;

import com.cosmos.friendsMatching.common.ErrorCode;
import com.cosmos.friendsMatching.exception.BusinessException;
import com.cosmos.friendsMatching.pojo.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.cosmos.friendsMatching.service.impl.UserServiceImpl.USER_LOGIN_STATE;


/**
 * @author  CosmosBackpacker
 * 此类是通过处理Session获取相关信息
 *
 */

@Component
@Slf4j
public class MySessionUtil {


    public User getUserInfo(HttpServletRequest request) {
        //从session中获取用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH, "未登录");
        }

        return (User) userObj;
    }


    public boolean isAdmin(HttpServletRequest request) {
        //从session中获取用户信息
        User user = this.getUserInfo(request);

        return user.getRole() == 1;
    }


}
