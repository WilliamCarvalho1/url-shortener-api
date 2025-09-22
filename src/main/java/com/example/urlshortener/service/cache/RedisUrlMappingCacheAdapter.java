package com.example.urlshortener.service.cache;

import com.example.urlshortener.model.UrlMapping;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisUrlMappingCacheAdapter implements UrlMappingCachePort {
    private final StringRedisTemplate redisTemplate;

    public RedisUrlMappingCacheAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void cache(UrlMapping mapping) {
        redisTemplate.opsForValue().set(
                String.valueOf(mapping.getCode()),
                mapping.getOriginalUrl(),
                Duration.ofDays(30)
        );
    }

    @Override
    public Optional<UrlMapping> get(Long code) {
        String url = redisTemplate.opsForValue().get(String.valueOf(code));
        return url != null
                ? Optional.of(UrlMapping.builder().code(code).originalUrl(url).build())
                : Optional.empty();
    }
}