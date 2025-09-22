package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.client.UrlUrlShorteningClientImpl;
import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateShortUrlUseCaseImplTest {

    private static final String ORIGINAL_URL = "http://example.com";
    public static final String SHORT_URL = "http://short.url/123";
    private static final Long CODE = 123L;

    @InjectMocks
    private CreateShortUrlUseCaseImpl createShortUrlUseCase;

    @Mock
    private UrlMappingRepository repository;

    @Mock
    private UrlUrlShorteningClientImpl client;


    @Test
    void createShortUrlSuccess() {

        when(repository.findByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());

        LinkResponse linkResponse = LinkResponse.builder()
                .id(CODE)
                .shortUrl(SHORT_URL)
                .originalUrl(ORIGINAL_URL)
                .build();

        when(client.shortenUrl(anyString()))
                .thenReturn(ResponseEntity.ok(linkResponse));

        ShortenResponse response = createShortUrlUseCase.createShortUrl(ORIGINAL_URL);

        assertEquals(CODE, response.getCode());
        assertEquals(SHORT_URL, response.getShortUrl());
        verify(repository).save(any(UrlMapping.class));
    }

    @Test
    void createShortUrlInvalidUrlThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createShortUrlUseCase.createShortUrl("invalid-url")
        );

        assertTrue(ex.getMessage().startsWith("Invalid URL"));
    }

    @Test
    void createShortUrl_throwsUrISyntaxException() {
        // This wrong URL will cause new URI() to throw URISyntaxException
        String badUrl = "http://ex ample.com";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createShortUrlUseCase.createShortUrl(badUrl)
        );

        assertTrue(ex.getMessage().startsWith("Invalid URL: "));
    }

    @Test
    void createShortMapping_existingUrl_returnsExisting() {
        UrlMapping mapping = UrlMapping.builder()
                .code(CODE)
                .shortUrl(SHORT_URL)
                .originalUrl(ORIGINAL_URL)
                .build();
        when(repository.findByOriginalUrl(ORIGINAL_URL))
                .thenReturn(Optional.of(mapping));

        ShortenResponse response = createShortUrlUseCase.createShortUrl(ORIGINAL_URL);

        assertEquals(CODE, response.getCode());
        assertEquals(SHORT_URL, response.getShortUrl());
        verify(repository, never()).save(any());
        verify(client, never()).shortenUrl(anyString());
    }

    @Test
    void createShortUrlDatabaseErrorOnFind() {
        when(repository.findByOriginalUrl(anyString()))
                .thenThrow(new DataAccessException("DB error") {
                });

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> createShortUrlUseCase.createShortUrl(ORIGINAL_URL)
        );
        assertTrue(ex.getMessage().contains("Database error"));
    }

    @Test
    void createShortUrlDatabaseErrorOnSave() {
        when(repository.findByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());

        LinkResponse linkResponse = LinkResponse.builder()
                .id(CODE)
                .shortUrl(SHORT_URL)
                .originalUrl(ORIGINAL_URL)
                .build();
        when(client.shortenUrl(anyString()))
                .thenReturn(ResponseEntity.ok(linkResponse));

        doThrow(new DataAccessException("DB save error") {
        })
                .when(repository).save(any(UrlMapping.class));

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> createShortUrlUseCase.createShortUrl(ORIGINAL_URL)
        );
        assertTrue(ex.getMessage().contains("Database error"));
    }

    @Test
    void createShortUrlThrowsRestClientException() {
        when(repository.findByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());

        when(client.shortenUrl(anyString()))
                .thenThrow(new RestClientException("Client error"));

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> createShortUrlUseCase.createShortUrl(ORIGINAL_URL)
        );
        assertTrue(ex.getMessage().contains("Failed to shorten URL"));
    }

    @Test
    void createShortUrlThrowsUrlShorteningServiceException() {
        when(repository.findByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());

        when(client.shortenUrl(anyString()))
                .thenReturn(ResponseEntity.ok(null));

        UrlShorteningServiceException ex = assertThrows(
                UrlShorteningServiceException.class,
                () -> createShortUrlUseCase.createShortUrl(ORIGINAL_URL)
        );
        assertTrue(ex.getMessage().contains("Failed to shorten URL"));
    }

}