package com.example.urlshortener.controller;

import com.example.urlshortener.api.ShortenRequest;
import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.api.UrlResponse;
import com.example.urlshortener.service.CreateShortUrlUseCase;
import com.example.urlshortener.service.ResolveUrlUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/api/v1"))
public class UrlShorteningControllerImpl implements UrlShorteningController {

    private static final Logger log = LoggerFactory.getLogger(UrlShorteningControllerImpl.class);

    private final CreateShortUrlUseCase createShortUrlUseCase;
    private final ResolveUrlUseCase resolveUrlUseCase;

    @Autowired
    public UrlShorteningControllerImpl(ResolveUrlUseCase resolveUrlUseCase, CreateShortUrlUseCase createShortUrlUseCase) {
        this.resolveUrlUseCase = resolveUrlUseCase;
        this.createShortUrlUseCase = createShortUrlUseCase;
    }

    @Override
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> create(@Valid @RequestBody ShortenRequest request) {

        log.info("Received request to shorten URL: {}", request.getUrl());
        ShortenResponse mapping = createShortUrlUseCase.createShortUrl(request.getUrl());
        log.info("Short URL created: code = {}, shortUrl = {}", mapping.getCode(), mapping.getShortUrl());

        return ResponseEntity.ok(mapping);
    }

    @Override
    @GetMapping("/resolve/{code}")
    public ResponseEntity<UrlResponse> resolve(@NotNull @PathVariable("code") Long code) {
        log.info("Received request to resolve code: {}", code);
        UrlResponse response = resolveUrlUseCase.resolveByCode(code);
        log.info("Original URL resolved: {}", response.getOriginalUrl());

        return ResponseEntity.ok(response);
    }

}
