package com.cosmos.friendsMatching.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.RedissonReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
@Slf4j
public class RedissonConfig {

    private String host;
    private String port;
    private String password;

    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();

        String redisAddress = String.format("redis://%s:%s", host, port);
//       / String redisAddress = "redis://localhost:6379";
        log.info("redisAddress:{}", redisAddress);
        //设置Redis的配置,使用1号数据库
        config.useSingleServer().setAddress(redisAddress).setDatabase(1);

        config.useSingleServer().setPassword(password);

        //2.创建实例
        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

}
