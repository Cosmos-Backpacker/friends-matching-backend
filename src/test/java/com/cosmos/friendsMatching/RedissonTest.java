package com.cosmos.friendsMatching;


import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.RedissonList;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {


    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {

        //与本地数据结构进行对比
        //List  数据存储在JVM内存中
        List<String> list = new ArrayList<>();
        list.add("Cosmos");
        list.add("Cosmos2");
        list.add("Cosmos3");
        for (String s : list) {
            System.out.println(s);
        }

        //获取列表api的时候需要设置一个键，

        RList<String> rList = redissonClient.getList("test-list");
//        rList.add("Cosmos4");
//        rList.add("Cosmos5");
//        rList.add("Cosmos36");
//        for (String s : rList) {
//            System.out.println(s);
//
//        }
        rList.remove(0);
        //Map


        //Set


        //Stack


    }


}
