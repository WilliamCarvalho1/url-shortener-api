package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.service.UrlShorteningService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlShorteningController {

    private static final Logger log = LoggerFactory.getLogger(UrlShorteningController.class);

    private final UrlShorteningService service;

    public UrlShorteningController(UrlShorteningService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/shorten")
    public ResponseEntity<ShortenResponse> createShortUrl(@Valid @RequestBody ShortenRequest request) {

        log.info("Received request to shorten URL: {}", request.getUrl());
        ShortenResponse mapping = service.createShortUrl(request.getUrl());
        log.info("Short URL created: code={}, shortUrl={}", mapping.getCode(), mapping.getShortUrl());

        return ResponseEntity.ok(new ShortenResponse(mapping.getCode(), mapping.getShortUrl()));
    }

    @GetMapping("/api/v1/resolve/{code}")
    public ResponseEntity<String> resolve(@NotNull @PathVariable("code") Long code) {
        log.info("Received request to resolve code: {}", code);
        return service.resolveByCode(code)
                .map(m -> {
                    log.info("Original URL found for code {}: {}", code, m.getOriginalUrl());
                    return ResponseEntity.ok(m.getOriginalUrl());
                })
                .orElseGet(() -> {
                    log.warn("No URL found for code: {}", code);
                    return ResponseEntity.notFound().build();
                });
    }

}
