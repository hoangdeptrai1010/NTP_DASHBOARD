package com.hospital.dashboard.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.hospital.dashboard.config.AppProperties;

import java.time.Duration;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private final AppProperties appProperties;

    public RefreshTokenService(StringRedisTemplate redisTemplate, AppProperties appProperties) {
        this.redisTemplate = redisTemplate;
        this.appProperties = appProperties;
    }

    public void store(String username, String refreshToken) {
        redisTemplate.opsForValue().set(
            "refresh_token:" + refreshToken, 
            username, 
            Duration.ofDays(appProperties.jwt().refreshTokenDays())
        );
    }

    public Optional<String> consume(String refreshToken) {
        String key = "refresh_token:" + refreshToken;
        String username = redisTemplate.opsForValue().get(key);
        if (username != null) {
            redisTemplate.delete(key);
        }
        return Optional.ofNullable(username);
    }
}
