package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.cache.UrlMappingCachePort;
import com.example.urlshortener.service.db.UrlMappingFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveUrlUseCaseImplTest {

    private static final String ORIGINAL_URL = "http://example.com";
    private static final String SHORT_URL = "http://short.url/123";
    private static final Long CODE = 123L;

    private ResolveUrlUseCase resolveUrlUseCase;

    @Mock
    private UrlMappingFinder finder;

    @Mock
    private UrlMappingCachePort cachePort;

    @BeforeEach
    void setUp() {
        resolveUrlUseCase = new ResolveUrlUseCaseImpl(cachePort, finder);
    }

    @Test
    void resolveByCodeCachedResponse() {
        UrlMapping mapping = UrlMapping.builder()
                .code(CODE)
                .originalUrl(ORIGINAL_URL)
                .shortUrl(SHORT_URL)
                .build();

        when(cachePort.findByCode(CODE))
                .thenReturn(Optional.of(mapping));

        UrlResponse result = resolveUrlUseCase.resolveByCode(CODE);

        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        verify(cachePort).findByCode(CODE);
        verify(finder, never()).findExistingMappingByCode(anyLong());
    }

    @Test
    void resolveByCodeDbResponse() {
        when(cachePort.findByCode(CODE))
                .thenReturn(Optional.empty());

        UrlMapping mapping = UrlMapping.builder()
                .code(CODE)
                .shortUrl(SHORT_URL)
                .originalUrl(ORIGINAL_URL)
                .build();
        when(finder.findExistingMappingByCode(CODE))
                .thenReturn(Optional.of(mapping));
        doNothing()
                .when(cachePort).save(mapping);

        UrlResponse result = resolveUrlUseCase.resolveByCode(CODE);

        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        verify(cachePort).findByCode(CODE);
        verify(finder).findExistingMappingByCode(CODE);
        verify(cachePort).save(mapping);
    }

    @Test
    void resolveByCodeNotFound() {
        when(cachePort.findByCode(CODE))
                .thenReturn(Optional.empty());
        when(finder.findExistingMappingByCode(CODE))
                .thenReturn(Optional.empty());

        UrlNotFoundException ex = assertThrows(
                UrlNotFoundException.class,
                () -> resolveUrlUseCase.resolveByCode(CODE)
        );

        assertEquals(CODE.toString(), ex.getMessage());

        verify(cachePort).findByCode(CODE);
        verify(finder).findExistingMappingByCode(CODE);
        verify(cachePort, never()).save(any());
        verifyNoMoreInteractions(cachePort, finder);
    }

    @Test
    void resolveByCodeThrowsUrlShorteningServiceException() {
        when(cachePort.findByCode(CODE))
                .thenReturn(Optional.empty());
        when(finder.findExistingMappingByCode(CODE))
                .thenThrow(new DataAccessException("DB error") {
                });

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> resolveUrlUseCase.resolveByCode(CODE)
        );

        assertTrue(ex.getMessage().contains("Database error"));
        verify(cachePort).findByCode(CODE);
        verify(finder).findExistingMappingByCode(CODE);
    }
}