package com.example.urlshortener.service.db;

import com.example.urlshortener.model.UrlMapping;

import java.util.Optional;

public interface UrlMappingFinder {
    Optional<UrlMapping> findExistingMappingByUrl(String originalUrl);

    Optional<UrlMapping> findExistingMappingByCode(Long code);
}
