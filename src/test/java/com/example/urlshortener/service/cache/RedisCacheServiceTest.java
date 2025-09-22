package com.example.urlshortener.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisCacheServiceTest {

    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private RedisCacheService cacheService;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cacheService = new RedisCacheService(redisTemplate);
    }

    @Test
    void setShouldStoreValue() {
        String key = "testKey";
        String value = "testValue";
        Duration ttl = Duration.ofMinutes(5);

        cacheService.set(key, value, ttl);

        verify(valueOperations).set(key, value, ttl);
    }

    @Test
    void getShouldReturnValueIfPresent() {
        String key = "testKey";
        String value = "testValue";
        when(valueOperations.get(key))
                .thenReturn(value);

        Optional<String> result = cacheService.get(key);

        assertTrue(result.isPresent());
        assertEquals(value, result.get());
        verify(valueOperations).get(key);
    }

    @Test
    void getShouldReturnEmptyIfValueIsNull() {
        String key = "missingKey";
        when(valueOperations.get(key))
                .thenReturn(null);

        Optional<String> result = cacheService.get(key);

        assertFalse(result.isPresent());
        verify(valueOperations).get(key);
    }
}