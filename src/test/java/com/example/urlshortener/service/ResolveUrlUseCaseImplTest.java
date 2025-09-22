package com.example.urlshortener.service;

import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveUrlUseCaseImplTest {

    private static final String ORIGINAL_URL = "http://example.com";
    public static final String SHORT_URL = "http://short.url/123";
    private static final Long CODE = 123L;

    @InjectMocks
    private ResolveUrlUseCaseImpl resolveUrlUseCase;

    @Mock
    private UrlMappingRepository repository;

    @Test
    void resolveByCodeSuccess() {
        UrlMapping mapping = UrlMapping.builder()
                .code(CODE)
                .shortUrl(SHORT_URL)
                .originalUrl(ORIGINAL_URL)
                .build();
        when(repository.findByCode(CODE))
                .thenReturn(Optional.of(mapping));

        Optional<UrlMapping> result = resolveUrlUseCase.resolveByCode(CODE);

        assertTrue(result.isPresent());
        assertEquals(CODE, result.get().getCode());
        assertEquals(SHORT_URL, result.get().getShortUrl());
        assertEquals(ORIGINAL_URL, result.get().getOriginalUrl());
    }

    @Test
    void resolveByCodeNotFound() {
        when(repository.findByCode(CODE))
                .thenReturn(Optional.empty());

        Optional<UrlMapping> result = resolveUrlUseCase.resolveByCode(CODE);

        assertFalse(result.isPresent());
    }

    @Test
    void resolveByCodeThrowsUrlShorteningServiceException() {
        when(repository.findByCode(CODE))
                .thenThrow(new DataAccessException("DB error") {
                });

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> resolveUrlUseCase.resolveByCode(CODE)
        );
        assertTrue(ex.getMessage().contains("Database error"));
    }

}