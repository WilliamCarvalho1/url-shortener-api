package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.cache.UrlMappingCachePort;
import com.example.urlshortener.service.db.UrlMappingFinderImpl;
import com.example.urlshortener.service.db.UrlMappingPersister;
import com.example.urlshortener.service.external.ExternalShorteningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateShortUrlUseCaseImplTest {

    private static final String ORIGINAL_URL = "http://example.com";
    private static final String SHORT_URL = "http://short.url/abc";
    private static final Long CODE = 123L;

    @Mock
    UrlMappingCachePort cachePort;
    @Mock
    UrlMappingFinderImpl finder;
    @Mock
    ExternalShorteningService externalService;
    @Mock
    UrlMappingPersister persister;

    @InjectMocks
    CreateShortUrlUseCaseImpl useCase;

    UrlMapping mapping;
    LinkResponse linkResponse;

    @BeforeEach
    void setUp() {
        mapping = UrlMapping.builder()
                .code(CODE)
                .originalUrl(ORIGINAL_URL)
                .shortUrl(SHORT_URL)
                .build();
        linkResponse = LinkResponse.builder()
                .code(CODE)
                .shortUrl(SHORT_URL)
                .originalUrl(ORIGINAL_URL)
                .build();
    }

    @Test
    void returnsExistingMappingIfPresent() {
        when(finder.findExistingMappingByUrl(ORIGINAL_URL))
                .thenReturn(Optional.of(mapping));

        ShortenResponse response = useCase.createShortUrl(ORIGINAL_URL);

        assertEquals(SHORT_URL, response.getShortUrl());
        verify(finder).findExistingMappingByUrl(ORIGINAL_URL);
        verifyNoInteractions(externalService, persister, cachePort);
    }

    @Test
    void createsNewShortUrlIfNotPresent() {
        when(finder.findExistingMappingByUrl(ORIGINAL_URL))
                .thenReturn(Optional.empty());
        when(externalService.callExternalShortener(ORIGINAL_URL))
                .thenReturn(linkResponse);
        when(persister.saveFromResponse(any(UrlMapping.class)))
                .thenReturn(mapping);

        ShortenResponse response = useCase.createShortUrl(ORIGINAL_URL);

        assertEquals(SHORT_URL, response.getShortUrl());
        verify(finder).findExistingMappingByUrl(ORIGINAL_URL);
        verify(externalService).callExternalShortener(ORIGINAL_URL);
        verify(persister).saveFromResponse(any(UrlMapping.class));
        verify(cachePort).cache(mapping);
    }

    @Test
    void throwsExceptionIfExternalServiceReturnsNull() {
        when(finder.findExistingMappingByUrl(ORIGINAL_URL))
                .thenReturn(Optional.empty());
        when(externalService.callExternalShortener(ORIGINAL_URL))
                .thenReturn(null);

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> useCase.createShortUrl(ORIGINAL_URL)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    void throwsExceptionIfPersisterFails() {
        when(finder.findExistingMappingByUrl(ORIGINAL_URL))
                .thenReturn(Optional.empty());
        when(externalService.callExternalShortener(ORIGINAL_URL))
                .thenReturn(linkResponse);
        when(persister.saveFromResponse(any(UrlMapping.class)))
                .thenThrow(new DataAccessException("DB error") {
                });

        assertThrows(
                DataAccessException.class,
                () -> useCase.createShortUrl(ORIGINAL_URL)
        );
    }
}