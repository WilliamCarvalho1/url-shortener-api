package com.example.urlshortener.cache;

import com.example.urlshortener.model.UrlMapping;

import java.util.Optional;

public interface UrlMappingCachePort {

    Optional<UrlMapping> findByCode(Long code);

    Optional<UrlMapping> findByOriginalUrl(String originalUrl);

    void save(UrlMapping urlMapping);
}