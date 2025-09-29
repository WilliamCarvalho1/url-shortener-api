package com.example.urlshortener.service.cache;

import com.example.urlshortener.cache.RedisUrlMappingCacheAdapter;
import com.example.urlshortener.cache.UrlMappingCachePort;
import com.example.urlshortener.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisUrlMappingCacheAdapterTest {

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
    void findByCodeShouldReturnMappingWhenPresent() {
        Long code = 123L;
        String originalUrl = "http://example.com/a";
        when(valueOperations.get("code:" + code))
                .thenReturn(originalUrl);

        Optional<UrlMapping> result = cacheService.findByCode(code);

        assertTrue(result.isPresent());
        assertEquals(code, result.get().getCode());
        assertEquals(originalUrl, result.get().getOriginalUrl());
        assertNull(result.get().getShortUrl());
        verify(valueOperations).get("code:123");
    }

    @Test
    void findByCodeShouldReturnEmptyWhenAbsent() {
        Long code = 999L;
        when(valueOperations.get("code:" + code))
                .thenReturn(null);

        Optional<UrlMapping> result = cacheService.findByCode(code);

        assertTrue(result.isEmpty());
        verify(valueOperations).get("code:999");
    }

    @Test
    void findByOriginalUrlShouldReturnParsedMappingWhenPresent() {
        String originalUrl = "http://example.com/page";
        Long expectedCode = 321L;
        String expectedShort = "http://sho.rt/xyz";
        String redisValue = expectedCode + "_" + expectedShort;

        when(valueOperations.get("url:" + originalUrl))
                .thenReturn(redisValue);

        Optional<UrlMapping> result = cacheService.findByOriginalUrl(originalUrl);

        assertTrue(result.isPresent());
        UrlMapping mapping = result.get();
        assertEquals(expectedCode, mapping.getCode());
        assertEquals(expectedShort, mapping.getShortUrl());
        assertNull(mapping.getOriginalUrl());
        verify(valueOperations).get("url:http://example.com/page");
    }

    @Test
    void findByOriginalUrlShouldReturnEmptyWhenMissing() {
        String originalUrl = "http://absent.com";
        when(valueOperations.get("url:" + originalUrl))
                .thenReturn(null);

        Optional<UrlMapping> result = cacheService.findByOriginalUrl(originalUrl);

        assertTrue(result.isEmpty());
        verify(valueOperations).get("url:http://absent.com");
    }

    @Test
    void saveShouldStoreBothDirectionsWithCorrectTTLAndFormat() {
        UrlMapping mapping = UrlMapping.builder()
                .code(555L)
                .originalUrl("http://host/path")
                .shortUrl("http://sho.rt/abc")
                .build();

        cacheService.save(mapping);

        // Capture both calls
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<TimeUnit> unitCaptor = ArgumentCaptor.forClass(TimeUnit.class);

        verify(valueOperations, times(2))
                .set(keyCaptor.capture(), valueCaptor.capture(), ttlCaptor.capture(), unitCaptor.capture());

        List<String> keys = keyCaptor.getAllValues();
        List<String> values = valueCaptor.getAllValues();

        assertTrue(keys.contains("code:555"));
        assertTrue(keys.contains("url:http://host/path"));

        // Original URL stored under code key
        int codeIdx = keys.indexOf("code:555");
        assertEquals("http://host/path", values.get(codeIdx));

        // code_shortUrl stored under url key
        int urlIdx = keys.indexOf("url:http://host/path");
        assertEquals("555_http://sho.rt/abc", values.get(urlIdx));

        // TTL assertions
        ttlCaptor.getAllValues().forEach(ttl -> assertEquals(30L, ttl));
        unitCaptor.getAllValues().forEach(unit -> assertEquals(TimeUnit.DAYS, unit));
    }
}