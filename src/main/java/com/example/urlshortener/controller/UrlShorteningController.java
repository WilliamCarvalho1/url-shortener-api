package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.dto.UrlResponse;
import org.springframework.http.ResponseEntity;

public interface UrlShorteningController {
    ResponseEntity<ShortenResponse> create(ShortenRequest request);

    ResponseEntity<UrlResponse> resolve(Long code);
}
