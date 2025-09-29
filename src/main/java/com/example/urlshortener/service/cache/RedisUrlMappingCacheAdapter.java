package com.example.urlshortener.service.cache;

import com.example.urlshortener.model.UrlMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.urlshortener.util.RedisResponseMapper.getCodeFromRedisResponse;
import static com.example.urlshortener.util.RedisResponseMapper.getShortUrlFromRedisResponse;

@Component
public class RedisUrlMappingCacheAdapter implements UrlMappingCachePort {

    private static final String CODE_TO_URL_KEY = "code:%s";       // shortCode → originalUrl
    private static final String URL_TO_CODE_KEY = "url:%s";         // originalUrl → shortCode

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisUrlMappingCacheAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<UrlMapping> findByCode(Long code) {
        String url = redisTemplate.opsForValue().get(String.format(CODE_TO_URL_KEY, code));
        return url != null
                ? Optional.of(UrlMapping.builder().code(code).originalUrl(url).build())
                : Optional.empty();
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrl(String originalUrl) {
        String redisResponse = redisTemplate.opsForValue().get(String.format(URL_TO_CODE_KEY, originalUrl));
        if (redisResponse != null) {
            return Optional.of(UrlMapping.builder()
                    .code(getCodeFromRedisResponse(redisResponse))
                    .shortUrl(getShortUrlFromRedisResponse(redisResponse))
                    .build()
            );
        }
        return Optional.empty();
    }

    @Override
    public void save(UrlMapping urlMapping) {
        // store both directions
        redisTemplate.opsForValue().set(
                String.format(CODE_TO_URL_KEY, urlMapping.getCode()),
                urlMapping.getOriginalUrl(),
                30, TimeUnit.DAYS
        );

        redisTemplate.opsForValue().set(
                String.format(URL_TO_CODE_KEY, urlMapping.getOriginalUrl()),
                urlMapping.getCode() + "_" + urlMapping.getShortUrl(),
                30, TimeUnit.DAYS
        );
    }

}