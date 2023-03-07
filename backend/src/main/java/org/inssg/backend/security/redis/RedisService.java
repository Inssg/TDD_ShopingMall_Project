package org.inssg.backend.security.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;


    //Redis 에서 해당 User email로 저장된 RefreshToken 삭제
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    //해당 Access Token 유효시간 가지고 와서 Blacklist로 저장
    public void setBlackListValues(String key, String data, Long expiration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key,data,expiration, TimeUnit.MILLISECONDS);
    }

    public void setValues(String key, String data, int expiration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data, (long)expiration, TimeUnit.MILLISECONDS);
    }

    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public boolean hasKeyBlackList(String key) {
        return redisTemplate.hasKey(key);
    }
}
