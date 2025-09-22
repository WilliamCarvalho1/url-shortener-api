package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.client.UrlShorteningClient;
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
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Optional;

import static com.example.urlshortener.util.UrlValidator.validateUrl;

@Service
public class CreateShortUrlUseCaseImpl implements CreateShortUrlUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateShortUrlUseCaseImpl.class);

    private final UrlShorteningClient client;
    private final UrlMappingRepository repository;
    private final CacheService cacheService;

    @Autowired
    public CreateShortUrlUseCaseImpl(UrlMappingRepository repository, UrlShorteningClient client, CacheService cacheService) {
        this.repository = repository;
        this.client = client;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public ShortenResponse createShortUrl(String originalUrl) {
        validateUrl(originalUrl);

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

            cacheService.set(String.valueOf(mapping.getCode()), mapping.getOriginalUrl(), Duration.ofDays(30));

            return ShortenResponse.builder()
                    .code(response.getId())
                    .shortUrl(response.getShortUrl())
                    .build();
        } catch (DataAccessException e) {
            throw new UrlShorteningServiceException("Database error while saving data", e);
        }
    }
}