package com.example.urlshortener.service;

import com.example.urlshortener.model.UrlMapping;

import java.util.Optional;

public interface ResolveUrlUseCase {
    Optional<UrlMapping> resolveByCode(Long code);
}
