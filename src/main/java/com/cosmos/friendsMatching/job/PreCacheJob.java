package com.cosmos.friendsMatching.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmos.friendsMatching.pojo.User;
import com.cosmos.friendsMatching.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PreCacheJob {

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    //重点用户列表,只需要给一些重点用户添加缓存
    List<Long> majorUser = List.of(1L);


    @Scheduled(cron = "0 0 0 ? * ?  ")//每日0点执行这个任务
    public void preCacheJob() {
        log.info("定时任务开始执行");

        //设置一个键
        RLock lock = redissonClient.getLock("friendsMatching:preCache:doCache:lock");
        //等待时间0（指的是多个线程同时抢占，如果没有抢到多长时间之后再来，0代表不来了），释放时间（锁的过期时间）
        try {
            System.out.println("doLock" + Thread.currentThread().getId());
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {    //抢到锁了返回true否则返回false
                ValueOperations valueOperations = redisTemplate.opsForValue();
                Page<User> userPage = new Page<>();

                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                userPage = userService.page(new Page<>(1, 10), queryWrapper);

                //存储通用缓存

                valueOperations.set("cosmos:user:recommend:usual", userPage);


                //对于一些特殊用户单独设置缓存
                for (Long l : majorUser) {
                    String redisKey = String.format("friendsMatch:preCache:majorUser:%s", l);
                    //写缓存
                    try {
                        //设置过期时间为1天
                        valueOperations.set(redisKey, userPage, 1, TimeUnit.DAYS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }

                }

            } else {
                System.out.println("没有获取锁");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //放在这里防止中间出现了异常导致锁没有释放
            System.out.println("onLock" + Thread.currentThread().getId());
            if (lock.isHeldByCurrentThread()) { //判断这个锁是不是当前线程上的锁，为了解决只能释放自己上的锁
                //执行完成释放锁
                lock.unlock();
            }
        }
    }
}
