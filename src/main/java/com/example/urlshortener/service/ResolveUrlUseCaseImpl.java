package com.example.urlshortener.service;

import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.service.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
public class ResolveUrlUseCaseImpl implements ResolveUrlUseCase {
    private static final Logger log = LoggerFactory.getLogger(ResolveUrlUseCaseImpl.class);

    private final UrlMappingRepository repository;
    private final CacheService cacheService;

    @Autowired
    public ResolveUrlUseCaseImpl(UrlMappingRepository repository, CacheService cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UrlMapping> resolveByCode(Long code) {
        Optional<String> cachedUrl = cacheService.get(String.valueOf(code));

        if (cachedUrl.isPresent()) {
            UrlMapping cachedMapping = UrlMapping.builder()
                    .code(code)
                    .originalUrl(cachedUrl.get())
                    .build();
            return Optional.of(cachedMapping);
        } else {
            try {
                log.info("Resolving code: {}", code);
                Optional<UrlMapping> response = repository.findByCode(code);
                if (response.isPresent()) {
                    log.info("Found mapping for code {}: {}", code, response.get().getOriginalUrl());
                    cacheService.set(String.valueOf(response.get().getCode()), response.get().getOriginalUrl(), Duration.ofDays(30));
                } else {
                    log.warn("No mapping found for code: {}", code);
                }
                return response;
            } catch (DataAccessException e) {
                throw new UrlShorteningServiceException("Database error while retrieving data", e);
            }
        }
    }
}