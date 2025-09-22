package com.example.urlshortener.repository;

import com.example.urlshortener.model.UrlMapping;

import java.util.Optional;

public interface UrlMappingRepository {
    void save(UrlMapping mapping);

    Optional<UrlMapping> findByCode(Long code);

    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}
