package com.example.urlshortener.service;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.client.UrlShorteningClient;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlShorteningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
public class UrlShorteningService {

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
                throw new IllegalArgumentException("Invalid URL: missing scheme or host");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + e.getMessage(), e);
        }

        Optional<UrlMapping> dbResponse = repository.findByOriginalUrl(originalUrl);
        if (dbResponse.isPresent()) {
            return ShortenResponse.builder()
                    .code(dbResponse.get().getCode())
                    .shortUrl(dbResponse.get().getShortUrl())
                    .build();
        }

        LinkResponse response = client.shortenUrl(originalUrl).getBody();

        // Persist entity
        assert response != null;
        UrlMapping mapping = UrlMapping.builder()
                .code(response.getId())
                .shortUrl(response.getShortUrl())
                .originalUrl(response.getOriginalUrl())
                .build();

        repository.save(mapping);

        return ShortenResponse.builder()
                .code(response.getId())
                .shortUrl(response.getShortUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<UrlMapping> resolveByCode(Long code) {

        return repository.findByCode(code);
    }

}
