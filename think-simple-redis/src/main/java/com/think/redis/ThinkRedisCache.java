package com.think.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Date :2021/8/25
 * @Name :ThinkRedisCache
 * @Description : 请输入
 */
@Component
public class ThinkRedisCache {

    @Autowired
    RedisTemplate redisTemplate;

    public void function(){
//    redisTemplate.opsForValue().


    }
}
