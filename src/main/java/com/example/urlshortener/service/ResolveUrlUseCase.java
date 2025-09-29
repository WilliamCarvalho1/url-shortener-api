package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;

public interface ResolveUrlUseCase {
    UrlResponse resolveByCode(Long code);
}
