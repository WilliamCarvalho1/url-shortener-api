package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.CreateShortUrlUseCase;
import com.example.urlshortener.service.ResolveUrlUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShorteningControllerImplTest {

    private static final String ORIGINAL_URL = "http://example.com";
    public static final String SHORT_URL = "http://short.url/123";
    private static final Long CODE = 123L;

    @InjectMocks
    private UrlShorteningControllerImpl controller;

    @Mock
    private CreateShortUrlUseCase createShortUrlUseCase;
    @Mock
    private ResolveUrlUseCase resolveUrlUseCase;

    @Test
    void createSuccess() {
        ShortenResponse mockResponse = new ShortenResponse(CODE, SHORT_URL);
        when(createShortUrlUseCase.createShortUrl(anyString()))
                .thenReturn(mockResponse);

        ShortenRequest request = new ShortenRequest(ORIGINAL_URL);

        ResponseEntity<ShortenResponse> response = controller.create(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(CODE, response.getBody().getCode());
        assertEquals(SHORT_URL, response.getBody().getShortUrl());
    }

    @Test
    void resolveSuccess() {
        when(resolveUrlUseCase.resolveByCode(CODE))
                .thenReturn(java.util.Optional.of(UrlMapping.builder()
                        .code(CODE)
                        .originalUrl(ORIGINAL_URL)
                        .shortUrl(SHORT_URL)
                        .build())
                );

        ResponseEntity<String> response = controller.resolve(CODE);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(ORIGINAL_URL, response.getBody());
    }

    @Test
    void resolveNotFound() {
        when(resolveUrlUseCase.resolveByCode(CODE)).thenReturn(java.util.Optional.empty());

        ResponseEntity<String> response = controller.resolve(CODE);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

}