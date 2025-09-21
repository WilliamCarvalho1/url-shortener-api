package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.client.UrlShorteningClient;
import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlShorteningRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
public class UrlShorteningService {
    private static final Logger log = LoggerFactory.getLogger(UrlShorteningService.class);

    private final UrlShorteningRepository repository;
    private final UrlShorteningClient client;

    public UrlShorteningService(UrlShorteningRepository repository, UrlShorteningClient client) {
        this.repository = repository;
        this.client = client;
    }

    @Transactional
    public ShortenResponse createShortUrl(String originalUrl) {
        try {
            URI uri = new URI(originalUrl);
            String scheme = uri.getScheme();
            if (scheme == null || uri.getHost() == null ||
                    !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                log.warn("Invalid URL: missing scheme or host - {}", originalUrl);
                throw new IllegalArgumentException("Invalid URL: missing scheme or host");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + e.getMessage(), e);
        }

        try {
            Optional<UrlMapping> dbResponse = repository.findByOriginalUrl(originalUrl);
            if (dbResponse.isPresent()) {
                log.info("URL already shortened: {} -> {}", originalUrl, dbResponse.get().getShortUrl());
                return ShortenResponse.builder()
                        .code(dbResponse.get().getCode())
                        .shortUrl(dbResponse.get().getShortUrl())
                        .build();
            }
        } catch (DataAccessException e) {
            throw new UrlShorteningServiceException("Database error while retrieving data", e);
        }

        LinkResponse response;
        try {
            log.info("Calling external shortening service for: {}", originalUrl);
            response = client.shortenUrl(originalUrl).getBody();
            if (response == null) {
                throw new UrlShorteningServiceException("Failed to shorten URL: external service error");
            }
        } catch (RestClientException e) {
            throw new UrlShorteningServiceException("Failed to shorten URL: external service error", e);
        }

        try {
            UrlMapping mapping = UrlMapping.builder()
                    .code(response.getId())
                    .shortUrl(response.getShortUrl())
                    .originalUrl(response.getOriginalUrl())
                    .build();

            repository.save(mapping);
            log.info("Short URL created and saved: {} -> {}", originalUrl, response.getShortUrl());

            return ShortenResponse.builder()
                    .code(response.getId())
                    .shortUrl(response.getShortUrl())
                    .build();
        } catch (DataAccessException e) {
            throw new UrlShorteningServiceException("Database error while saving data", e);
        }
    }

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