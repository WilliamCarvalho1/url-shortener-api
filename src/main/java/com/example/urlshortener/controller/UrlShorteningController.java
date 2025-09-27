package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.api.UrlResponse;
import org.springframework.http.ResponseEntity;

public interface UrlShorteningController {
    ResponseEntity<ShortenResponse> create(ShortenRequest request);

    ResponseEntity<UrlResponse> resolve(Long code);
}
