package com.example.urlshortener.service.cache;

import com.example.urlshortener.model.UrlMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.urlshortener.util.RedisResponseMapper.concatenateCodeAndShortUrl;
import static com.example.urlshortener.util.RedisResponseMapper.deconcatenateCodeAndShortUrl;

@Component
public class RedisUrlMappingCacheAdapter implements UrlMappingCachePort {
    private static final String CODE_PATTERN = "code:%s";
    private static final String URL_PATTERN = "url:%s";

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisUrlMappingCacheAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<UrlMapping> findByCode(Long code) {
        String cachedOriginalUrl = redisTemplate.opsForValue().get(codeKey(code));
        return cachedOriginalUrl == null
                ? Optional.empty()
                : Optional.of(UrlMapping.builder().code(code).originalUrl(cachedOriginalUrl).build());
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrl(String originalUrl) {
        String cachedCodeAndShortLink = redisTemplate.opsForValue().get(originalUrlKey(originalUrl));
        if (cachedCodeAndShortLink == null)
            return Optional.empty();

        UrlMapping urlMapping = deconcatenateCodeAndShortUrl(cachedCodeAndShortLink);

        return Optional.of(urlMapping);
    }

    @Override
    public void save(UrlMapping mapping) {
        final int ttl = 30;

        redisTemplate.opsForValue().set(
                codeKey(mapping.getCode()),
                mapping.getOriginalUrl(),
                ttl, TimeUnit.DAYS
        );

        redisTemplate.opsForValue().set(
                originalUrlKey(mapping.getOriginalUrl()),
                concatenateCodeAndShortUrl(mapping),
                ttl, TimeUnit.DAYS
        );
    }

    private static String codeKey(Long code) {
        return String.format(CODE_PATTERN, code);
    }

    private static String originalUrlKey(String originalUrl) {
        return String.format(URL_PATTERN, originalUrl);
    }
}