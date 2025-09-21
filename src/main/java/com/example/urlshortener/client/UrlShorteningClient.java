package com.example.urlshortener.client;

import com.example.urlshortener.exception.UrlShorteningClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class UrlShorteningClient {
    private final String apiKey;
    private final String domain;
    private final String workspaceId;
    private final RestTemplate restTemplate;

    @Autowired
    public UrlShorteningClient(
            @Value("${url-shortener.apiKey}") String apiKey,
            @Value("${url-shortener.domain}") String domain,
            @Value("${url-shortener.workspaceId}") String workspaceId,
            RestTemplate restTemplate
    ) {
        this.apiKey = apiKey;
        this.domain = domain;
        this.workspaceId = workspaceId;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<LinkResponse> shortenUrl(String url) {
        String apiUrl = domain + "/api/v1/link?api_key=" + apiKey;

        Map<String, String> body = new HashMap<>();
        body.put("url", url);
        body.put("workspace_id", workspaceId);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body);

        try {
            return restTemplate.exchange(apiUrl, HttpMethod.POST, entity, LinkResponse.class);
        } catch (RestClientException e) {
            throw new UrlShorteningClientException("Failed to call external URL shortening service", e);
        }
    }

}
