package com.example.urlshortener.service.external;

import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.client.UrlShorteningClient;
import com.example.urlshortener.exception.UrlShorteningServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class ExternalShorteningServiceImpl implements ExternalShorteningService {
    private static final Logger log = LoggerFactory.getLogger(ExternalShorteningServiceImpl.class);
    private static final String CB_NAME = "shortenerClient";
    private final UrlShorteningClient client;

    public ExternalShorteningServiceImpl(UrlShorteningClient client) {
        this.client = client;
    }

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallback")
    @Retry(name = CB_NAME)
    public LinkResponse callExternalShortener(String originalUrl) {
        try {
            log.info("Calling external shortening service for: {}", originalUrl);
            var response = client.shortenUrl(originalUrl).getBody();
            if (response == null) {
                throw new UrlShorteningServiceException("External service returned null response");
            }
            return response;
        } catch (RestClientException e) {
            throw new UrlShorteningServiceException("Error calling external shortening service", e);
        }
    }

    @SuppressWarnings("unused")
    private LinkResponse fallback(String originalUrl, Throwable t) {
        log.warn("Fallback triggered for URL: {} due to: {}", originalUrl, t.getMessage());
        LinkResponse response = new LinkResponse();
        response.setOriginalUrl(originalUrl);
        response.setShortUrl("fallback-" + originalUrl.hashCode());
        return response;
    }
}
