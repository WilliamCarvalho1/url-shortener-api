package com.example.urlshortener.client;

import org.springframework.http.ResponseEntity;

public interface UrlShorteningClient {
    ResponseEntity<LinkResponse> shortenUrl(String url);
}
