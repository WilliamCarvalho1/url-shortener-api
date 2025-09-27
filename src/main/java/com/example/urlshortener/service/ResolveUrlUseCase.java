package com.example.urlshortener.service;

import com.example.urlshortener.api.UrlResponse;

public interface ResolveUrlUseCase {
    UrlResponse resolveByCode(Long code);
}
