package com.example.urlshortener.service.cache;

import com.example.urlshortener.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlMappingCachePortTest {

    private ValueOperations<String, String> valueOperations;
    private UrlMappingCachePort cacheService;

    @BeforeEach
    void setUp() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cacheService = new RedisUrlMappingCacheAdapter(redisTemplate);
    }

    @Test
    void setShouldStoreValue() {
        UrlMapping mapping = UrlMapping.builder()
                .code(127L)
                .originalUrl("http://example.com")
                .shortUrl("http://short.url/123")
                .build();

        cacheService.cache(mapping);

        verify(valueOperations).set(
                String.valueOf(mapping.getCode()),
                mapping.getOriginalUrl(),
                Duration.ofDays(30)
        );
    }

    @Test
    void getShouldReturnValueIfPresent() {
        Long key = 10L;
        String value = "testValue";
        when(valueOperations.get(String.valueOf(key)))
                .thenReturn(value);

        Optional<UrlMapping> result = cacheService.get(key);

        assertTrue(result.isPresent());
        assertEquals(key, result.get().getCode());
        verify(valueOperations).get(String.valueOf(key));
    }

    @Test
    void getShouldReturnEmptyIfValueIsNull() {
        Long key = 10L;
        when(valueOperations.get(String.valueOf(key)))
                .thenReturn(null);

        Optional<UrlMapping> result = cacheService.get(key);

        assertFalse(result.isPresent());
        verify(valueOperations).get(String.valueOf(key));
    }
}