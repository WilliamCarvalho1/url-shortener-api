package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.cache.UrlMappingCachePort;
import com.example.urlshortener.service.db.UrlMappingFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.urlshortener.util.UrlMappingMapper.urlMappingToUrlResponse;

@Service
public class ResolveUrlUseCaseImpl implements ResolveUrlUseCase {
    private static final Logger log = LoggerFactory.getLogger(ResolveUrlUseCaseImpl.class);

    private final UrlMappingCachePort cachePort;
    private final UrlMappingFinder finder;

    @Autowired
    public ResolveUrlUseCaseImpl(UrlMappingCachePort cachePort, UrlMappingFinder finder) {
        this.cachePort = cachePort;
        this.finder = finder;
    }

    @Override
    @Transactional(readOnly = true)
    public UrlResponse resolveByCode(Long code) {
        try {
            Optional<UrlMapping> cached = cachePort.findByCode(code);
            if (cached.isPresent()) {
                log.info("Cache hit for code {} -> {}", code, cached.get().getOriginalUrl());
                return urlMappingToUrlResponse(cached.get());
            }

            Optional<UrlMapping> dbStored = finder.findExistingMappingByCode(code);
            if (dbStored.isPresent()) {
                log.info("Cache miss, storing in cache: {} -> {}", code, dbStored.get().getOriginalUrl());
                cachePort.save(dbStored.get());
                return urlMappingToUrlResponse(dbStored.get());
            }
        } catch (DataAccessException ex) {
            throw new UrlShorteningServiceException("Database error: " + ex.getMessage(), ex);
        }
        throw new UrlNotFoundException(code.toString());
    }
}