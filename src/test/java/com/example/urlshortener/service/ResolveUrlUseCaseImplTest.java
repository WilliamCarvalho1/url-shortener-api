package com.example.urlshortener.service;

import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.service.cache.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveUrlUseCaseImplTest {

    private static final String ORIGINAL_URL = "http://example.com";
    public static final String SHORT_URL = "http://short.url/123";
    private static final Long CODE = 123L;

    @Mock
    private UrlMappingRepository repository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ResolveUrlUseCaseImpl resolveUrlUseCase;

    @BeforeEach
    void setUp() {
        resolveUrlUseCase = new ResolveUrlUseCaseImpl(repository, cacheService);
    }

    @Test
    void resolveByCodeCachedResponse() {
        when(cacheService.get(String.valueOf(CODE)))
                .thenReturn(Optional.of(ORIGINAL_URL));

        Optional<UrlMapping> result = resolveUrlUseCase.resolveByCode(CODE);

        assertTrue(result.isPresent());
        assertEquals(CODE, result.get().getCode());
        assertEquals(ORIGINAL_URL, result.get().getOriginalUrl());
        verify(cacheService).get(String.valueOf(CODE));
        verify(repository, never()).findByCode(anyLong());
    }

    @Test
    void resolveByCodeDbResponse() {
        when(cacheService.get(String.valueOf(CODE)))
                .thenReturn(Optional.empty());

        UrlMapping mapping = UrlMapping.builder()
                .code(CODE)
                .shortUrl(SHORT_URL)
                .originalUrl(ORIGINAL_URL)
                .build();
        when(repository.findByCode(CODE))
                .thenReturn(Optional.of(mapping));
        doNothing().when(cacheService)
                .set(String.valueOf(CODE), ORIGINAL_URL, Duration.ofDays(30));

        Optional<UrlMapping> result = resolveUrlUseCase.resolveByCode(CODE);

        assertTrue(result.isPresent());
        assertEquals(CODE, result.get().getCode());
        assertEquals(SHORT_URL, result.get().getShortUrl());
        assertEquals(ORIGINAL_URL, result.get().getOriginalUrl());
        verify(cacheService).get(String.valueOf(CODE));
        verify(repository).findByCode(CODE);
        verify(cacheService).set(String.valueOf(CODE), ORIGINAL_URL, Duration.ofDays(30));
    }

    @Test
    void resolveByCodeNotFound() {
        when(cacheService.get(String.valueOf(CODE)))
                .thenReturn(Optional.empty());
        when(repository.findByCode(CODE))
                .thenReturn(Optional.empty());

        Optional<UrlMapping> result = resolveUrlUseCase.resolveByCode(CODE);

        assertFalse(result.isPresent());
        verify(cacheService).get(String.valueOf(CODE));
        verify(repository).findByCode(CODE);
        verify(cacheService, never()).set(anyString(), anyString(), any());
    }

    @Test
    void resolveByCodeThrowsUrlShorteningServiceException() {
        when(cacheService.get(String.valueOf(CODE)))
                .thenReturn(Optional.empty());
        when(repository.findByCode(CODE))
                .thenThrow(new DataAccessException("DB error") {
                });

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> resolveUrlUseCase.resolveByCode(CODE)
        );
        assertTrue(ex.getMessage().contains("Database error"));
        verify(cacheService).get(String.valueOf(CODE));
        verify(repository).findByCode(CODE);
    }
}