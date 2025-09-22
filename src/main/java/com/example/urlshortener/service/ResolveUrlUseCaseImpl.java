package com.example.urlshortener.service;

import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ResolveUrlUseCaseImpl implements ResolveUrlUseCase {
    private static final Logger log = LoggerFactory.getLogger(ResolveUrlUseCaseImpl.class);

    private final UrlMappingRepository repository;

    @Autowired
    public ResolveUrlUseCaseImpl(UrlMappingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UrlMapping> resolveByCode(Long code) {
        try {
            log.info("Resolving code: {}", code);
            Optional<UrlMapping> result = repository.findByCode(code);
            if (result.isPresent()) {
                log.info("Found mapping for code {}: {}", code, result.get().getOriginalUrl());
            } else {
                log.warn("No mapping found for code: {}", code);
            }
            return result;
        } catch (DataAccessException e) {
            throw new UrlShorteningServiceException("Database error while retrieving data", e);
        }
    }
}