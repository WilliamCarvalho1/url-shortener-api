package com.example.urlshortener.service.db;

import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepositoryPort;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlMappingFinderImpl implements UrlMappingFinder {
    private final UrlMappingRepositoryPort repository;

    public UrlMappingFinderImpl(UrlMappingRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Optional<UrlMapping> findExistingMappingByUrl(String originalUrl) {
        try {
            return repository.findByOriginalUrl(originalUrl);
        } catch (DataAccessException e) {
            throw new UrlShorteningServiceException("Database error while retrieving data", e);
        }
    }

    @Override
    public Optional<UrlMapping> findExistingMappingByCode(Long code) {
        try {
            return repository.findByCode(code);
        } catch (DataAccessException e) {
            throw new UrlShorteningServiceException("Database error while retrieving data", e);
        }
    }
}
