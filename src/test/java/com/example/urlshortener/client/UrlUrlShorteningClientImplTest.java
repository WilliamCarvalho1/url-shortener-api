package com.example.urlshortener.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlUrlShorteningClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    private UrlUrlShorteningClientImpl client;

    @BeforeEach
    void setUp() {
        client = new UrlUrlShorteningClientImpl(
                "test-api-key",
                "http://test-domain.com",
                "test-workspace",
                restTemplate
        );
    }

    @Test
    void shortenUrlSuccess() {
        String url = "http://example.com";
        String expectedApiUrl = "http://test-domain.com/api/v1/link?api_key=test-api-key";

        LinkResponse mockResponse = new LinkResponse(1L, "shortUrl", "originalUrl");
        ResponseEntity<LinkResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(expectedApiUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(LinkResponse.class)
        )).thenReturn(responseEntity);

        ResponseEntity<LinkResponse> result = client.shortenUrl(url);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockResponse, result.getBody());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<Map<String, String>>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(expectedApiUrl),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(LinkResponse.class)
        );

        Map<String, String> body = entityCaptor.getValue().getBody();
        assertNotNull(body);
        assertEquals(url, body.get("url"));
        assertEquals("test-workspace", body.get("workspace_id"));
    }

    @Test
    void shortenUrlThrowsUrlShorteningClientExceptionOnRestClientException() {
        String url = "http://example.com";
        String expectedApiUrl = "http://test-domain.com/api/v1/link?api_key=test-api-key";

        when(restTemplate.exchange(
                eq(expectedApiUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(LinkResponse.class)
        )).thenThrow(new RestClientException("API error"));

        Exception ex = assertThrows(
                com.example.urlshortener.exception.UrlShorteningClientException.class,
                () -> client.shortenUrl(url)
        );
        assertTrue(ex.getMessage().contains("Failed to call external URL shortening service"));
        assertInstanceOf(RestClientException.class, ex.getCause());
    }

}