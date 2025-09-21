package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.service.UrlShorteningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShorteningController {

    private final UrlShorteningService service;

    public UrlShorteningController(UrlShorteningService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/shorten")
    public ResponseEntity<ShortenResponse> createShortUrl(@Valid @RequestBody ShortenRequest request) {

        ShortenResponse mapping = service.createShortUrl(request.getUrl());

        return ResponseEntity.ok(new ShortenResponse(mapping.getCode(), mapping.getShortUrl()));
    }

}
