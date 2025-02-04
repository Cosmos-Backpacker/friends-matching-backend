package com.cosmos.friendsMatching;

import java.time.LocalDateTime;


import com.cosmos.friendsMatching.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    @Autowired
    @Qualifier("redisTemplate") //这里需要指明需要注入哪一个
    public RedisTemplate template;

    @Test
    public void test() {

        //增
        ValueOperations valueOperations = template.opsForValue();
        valueOperations.set("cosmosString", "1111");
        valueOperations.set("cosmosInt", 2222);
        valueOperations.set("cosmosDouble", 2222.0);
        User user = new User();
        user.setId(0L);
        user.setUsername("cosmosUser");
        user.setUserAccount("cosmosUser");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setUserPassword("cosmosUser");
        user.setPhone("");
        user.setEmail("");
        user.setRole(0);
        user.setUserStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setTags("");
        user.setIsDelete(0);

        valueOperations.set("cosmosUser", user);
        Integer integer = (Integer) valueOperations.get("cosmosInt");

        //查
        System.out.println(valueOperations.get("cosmosString"));
        System.out.println(valueOperations.get("cosmosInt"));
        System.out.println(valueOperations.get("cosmosDouble"));
        System.out.println(valueOperations.get("cosmosUser"));


        template.delete("cosmosString");
        template.delete("cosmosInt");
        template.delete("cosmosDouble");
        template.delete("cosmosUser");


    }








}
