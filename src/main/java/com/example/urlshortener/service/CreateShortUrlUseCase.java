package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;

public interface CreateShortUrlUseCase {
    ShortenResponse createShortUrl(String originalUrl);
}
