package com.github.makewheels.ai_tools.user;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.ai_tools.system.redis.RedisKey;
import com.github.makewheels.ai_tools.system.redis.RedisService;
import com.github.makewheels.ai_tools.system.redis.RedisTime;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class UserRedisService {
    @Resource
    private RedisService redisService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public User getUserByToken(String token) {
        String json = (String) redisTemplate.opsForValue().get(token);
        return JSON.parseObject(json, User.class);
    }

    public void setUserByToken(User user) {
        redisService.set(RedisKey.token(user.getToken()), JSON.toJSONString(user),
                RedisTime.THIRTY_MINUTES);
    }

    public void delUserByToken(String token) {
        redisService.del(RedisKey.token(token));
    }
}
