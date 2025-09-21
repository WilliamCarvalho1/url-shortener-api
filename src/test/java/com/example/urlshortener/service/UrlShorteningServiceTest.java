package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.client.UrlShorteningClient;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlShorteningRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShorteningServiceTest {

    private static final String ORIGINAL_URL = "http://example.com";
    public static final String SHORT_URL = "http://short.url/123";
    private static final Long CODE = 123L;

    @InjectMocks
    private UrlShorteningService service;

    @Mock
    private UrlShorteningRepository repository;

    @Mock
    private UrlShorteningClient client;


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

        ShortenResponse response = service.createShortUrl(ORIGINAL_URL);

        assertEquals(CODE, response.getCode());
        assertEquals(SHORT_URL, response.getShortUrl());
        verify(repository).save(any(UrlMapping.class));
    }

    @Test
    void createShortUrlInvalidUrlThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createShortUrl("invalid-url")
        );

        assertTrue(ex.getMessage().startsWith("Invalid URL"));
    }

    @Test
    void createShortUrl_throwsUrISyntaxException() {
        // This wrong URL will cause new URI() to throw URISyntaxException
        String badUrl = "http://ex ample.com";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createShortUrl(badUrl)
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

        ShortenResponse response = service.createShortUrl(ORIGINAL_URL);

        assertEquals(CODE, response.getCode());
        assertEquals(SHORT_URL, response.getShortUrl());
        verify(repository, never()).save(any());
        verify(client, never()).shortenUrl(anyString());
    }
}