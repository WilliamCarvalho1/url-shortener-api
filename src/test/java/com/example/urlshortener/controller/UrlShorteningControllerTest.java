package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.service.UrlShorteningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShorteningControllerTest {

    @InjectMocks
    private UrlShorteningController controller;

    @Mock
    private UrlShorteningService service;

    @Test
    void createShortUrlUrlSuccess() {
        ShortenResponse mockResponse = new ShortenResponse(123L, "http://short.url/123");
        when(service.createShortUrl(anyString()))
                .thenReturn(mockResponse);

        ShortenRequest request = new ShortenRequest("http://example.com");

        ResponseEntity<ShortenResponse> response = controller.createShortUrl(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(123L, response.getBody().getCode());
        assertEquals("http://short.url/123", response.getBody().getShortUrl());
    }
}