package com.cosmos.friendsMatching;


import com.cosmos.friendsMatching.mapper.UserMapper;
import com.cosmos.friendsMatching.pojo.Result;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
class friendsMatchingApplicationTests {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private IUserService userService;

    @Test
    void testDigest() {
        String newPassword = DigestUtils.md5DigestAsHex(("cosmos" + "111111").getBytes());
        System.out.println(newPassword);

    }

    @Test
    public void userRegister() {
        Result result = userService.userRegister("ccjj12345", "12345678", "12345678");
        Assertions.assertEquals("恭喜你注册成功！！", result.getMsg());
    }


    @Test
    public void userLogin(HttpServletRequest request) {
////        Result result = userService.userLogin("1325645", "1234567810", request);
//        Assertions.assertEquals("恭喜你登录成功！！",result.getMsg());
    }


    @Test
    public void SelectUserByTags() {
        List<String> strs = Arrays.asList("python", "java");
        List<User> userList = userService.selectUserByTags(strs,1,20);
        System.out.println(userList);
        //Assert.assertNotNull(userLis);

    }


    @Test
    public void insertUserInfoByFor() {
        //利用StopWatch工具类帮助检测完成时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ArrayList<User> userArrayList = new ArrayList<User>();
        for (int i = 1100; i < 110000; i++) {
            User user = new User();
            user.setUsername("fakeUser");
            user.setUserAccount("fake1234" + i);
            user.setAvatarUrl("https://haowallpaper.com/link/common/file/previewFileImg/16167403474636160");
            user.setGender(0);
            user.setUserPassword("123456789");
            user.setPhone("19658584256");
            user.setEmail("15885632@qq.com");
            user.setUserStatus(0);
            userArrayList.add(user);

        }
        userService.saveBatch(userArrayList, 10000);

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }


    //采用异步编程
    @Test
    public void insertUserByAsync() {
        //利用StopWatch工具类帮助检测完成时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int batchSize = 5000; //5000个数组为一组
        int j = 0;
        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("fakeUser");
                user.setUserAccount("fake1234" + i + "two" + j);
                user.setAvatarUrl("https://haowallpaper.com/link/common/file/previewFileImg/16167403474636160");
                user.setGender(0);
                user.setUserPassword("123456789");
                user.setPhone("19658584256");
                user.setEmail("15885632@qq.com");
                user.setUserStatus(0);
                userList.add(user);
                if (j % 10000 == 0) {   //创建完一组
                    System.out.println(j);
                    break;
                }

            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName" + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            });

            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }


}
