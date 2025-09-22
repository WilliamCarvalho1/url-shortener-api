package com.example.urlshortener.service.db;

import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UrlMappingPersisterImpl implements UrlMappingPersister {
    private static final Logger log = LoggerFactory.getLogger(UrlMappingPersisterImpl.class);
    private final UrlMappingRepositoryPort repository;

    @Autowired
    public UrlMappingPersisterImpl(UrlMappingRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public UrlMapping saveFromResponse(UrlMapping urlMapping) {
        try {
            repository.save(urlMapping);
            log.info("Short URL created and saved: {} -> {}", urlMapping.getOriginalUrl(), urlMapping.getShortUrl());
            return urlMapping;
        } catch (DataAccessException e) {
            throw new UrlShorteningServiceException("Database error while saving mapping", e);
        }
    }

}
