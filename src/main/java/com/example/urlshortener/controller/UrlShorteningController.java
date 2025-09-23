package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import org.springframework.http.ResponseEntity;

public interface UrlShorteningController {
    ResponseEntity<ShortenResponse> create(ShortenRequest request);
    ResponseEntity<String> resolve(Long code);
}
