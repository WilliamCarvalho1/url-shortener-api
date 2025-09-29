package com.example.urlshortener.service;

import com.example.urlshortener.dto.ShortenResponse;

public interface CreateShortUrlUseCase {
    ShortenResponse createShortUrl(String originalUrl);
}
