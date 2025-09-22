package com.example.urlshortener.service.cache;

import com.example.urlshortener.model.UrlMapping;

import java.util.Optional;

public interface UrlMappingCachePort {

    void cache(UrlMapping mapping);

    Optional<UrlMapping> get(Long code);
}