package com.cosmos.friendsMatching.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.RedissonReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;
    private String port;


    @Bean
    public RedissonClient redissonClient() {

        StringBuilder sb = new StringBuilder();

        Config config = new Config();

        String redisAddress = "redis://localhost:6379";

        //设置Redis的配置,使用1号数据库
        config.useSingleServer().setAddress(redisAddress).setDatabase(1);

        //2.创建实例
        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

}
