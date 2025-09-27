package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.api.UrlResponse;
import com.example.urlshortener.service.CreateShortUrlUseCase;
import com.example.urlshortener.service.ResolveUrlUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShorteningControllerImplTest {

    private static final String ORIGINAL_URL = "http://example.com";
    private static final String SHORT_URL = "http://short.url/123";
    private static final Long CODE = 123L;

    private UrlShorteningController controller;

    @Mock
    private CreateShortUrlUseCase createShortUrlUseCase;
    @Mock
    private ResolveUrlUseCase resolveUrlUseCase;

    @BeforeEach
    void setUp() {
        controller = new UrlShorteningControllerImpl(resolveUrlUseCase, createShortUrlUseCase);
    }

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
        UrlResponse urlResponse = new UrlResponse(ORIGINAL_URL);
        when(resolveUrlUseCase.resolveByCode(CODE))
                .thenReturn(urlResponse);

        ResponseEntity<UrlResponse> response = controller.resolve(CODE);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(ORIGINAL_URL, response.getBody().getOriginalUrl());
    }
}