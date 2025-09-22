package com.example.urlshortener.service;

import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.cache.UrlMappingCachePort;
import com.example.urlshortener.service.db.UrlMappingFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ResolveUrlUseCaseImpl implements ResolveUrlUseCase {
    private static final Logger log = LoggerFactory.getLogger(ResolveUrlUseCaseImpl.class);

    private final UrlMappingCachePort cachePort;
    private final UrlMappingFinder finder;

    public ResolveUrlUseCaseImpl(UrlMappingCachePort cachePort, UrlMappingFinder finder) {
        this.cachePort = cachePort;
        this.finder = finder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UrlMapping> resolveByCode(Long code) {

        try {
            Optional<UrlMapping> cached = cachePort.get(code);
            if (cached.isPresent()) {
                log.info("Cache hit for code {} -> {}", code, cached.get().getOriginalUrl());
                return cached;
            }

            Optional<UrlMapping> dbResult = finder.findExistingMappingByCode(code);
            dbResult.ifPresent(mapping -> {
                log.info("Cache miss, storing in cache: {} -> {}", code, mapping.getOriginalUrl());
                cachePort.cache(mapping);
            });
            return dbResult;
        } catch (DataAccessException ex) {
            throw new UrlShorteningServiceException("Database error: " + ex.getMessage(), ex);
        }
    }
}