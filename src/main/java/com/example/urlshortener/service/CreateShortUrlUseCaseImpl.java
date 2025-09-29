package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.exception.UrlShorteningServiceException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.cache.UrlMappingCachePort;
import com.example.urlshortener.service.db.UrlMappingFinder;
import com.example.urlshortener.service.db.UrlMappingPersister;
import com.example.urlshortener.service.external.ExternalShorteningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.urlshortener.util.UrlMappingMapper.*;
import static com.example.urlshortener.util.UrlValidator.validateUrl;

@Service
public class CreateShortUrlUseCaseImpl implements CreateShortUrlUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateShortUrlUseCaseImpl.class);

    private final UrlMappingCachePort urlMappingCachePort;
    private final UrlMappingFinder finder;
    private final ExternalShorteningService externalService;
    private final UrlMappingPersister persister;

    @Autowired
    public CreateShortUrlUseCaseImpl(UrlMappingCachePort urlMappingCachePort,
                                     UrlMappingFinder finder,
                                     ExternalShorteningService externalService,
                                     UrlMappingPersister persister
    ) {

        this.urlMappingCachePort = urlMappingCachePort;
        this.finder = finder;
        this.externalService = externalService;
        this.persister = persister;
    }

    @Override
    @Transactional
    public ShortenResponse createShortUrl(String originalUrl) {
        validateUrl(originalUrl);

        Optional<UrlMapping> cachedMapping = urlMappingCachePort.findByOriginalUrl(originalUrl);
        if (cachedMapping.isPresent()) {
            return urlMappingToShortenResponseMapper(cachedMapping.get());
        }

        Optional<UrlMapping> existing = finder.findExistingMappingByUrl(originalUrl);
        if (existing.isPresent()) {
            log.info("URL already shortened: {} -> {}", originalUrl, existing.get().getShortUrl());
            return urlMappingToShortenResponseMapper(existing.get());
        }

        LinkResponse response = externalService.callExternalShortener(originalUrl);
        if (response == null) {
            throw new UrlShorteningServiceException("External service returned null response");
        }

        UrlMapping urlMapping = persister.saveFromResponse(linkResponseToUrlMappingMapper(response));
        urlMappingCachePort.save(urlMapping);

        return linkResponseToShortenResponseMapper(response);
    }
}