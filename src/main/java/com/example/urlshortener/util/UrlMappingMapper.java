package com.example.urlshortener.util;

import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.model.UrlMapping;

public class UrlMappingMapper {

    private UrlMappingMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static UrlMapping linkResponseToUrlMappingMapper(LinkResponse linkResponse) {
        return UrlMapping.builder()
                .code(linkResponse.getCode())
                .originalUrl(linkResponse.getOriginalUrl())
                .shortUrl(linkResponse.getShortUrl())
                .build();
    }

    public static ShortenResponse linkResponseToShortenResponseMapper(LinkResponse linkResponse) {
        return ShortenResponse.builder()
                .code(linkResponse.getCode())
                .shortUrl(linkResponse.getShortUrl())
                .build();
    }

    public static ShortenResponse urlMappingToShortenResponseMapper(UrlMapping urlMapping) {
        return ShortenResponse.builder()
                .code(urlMapping.getCode())
                .shortUrl(urlMapping.getShortUrl())
                .build();
    }

    public static UrlResponse urlMappingToUrlResponse(UrlMapping urlMapping) {
        return new UrlResponse(urlMapping.getOriginalUrl());
    }

}
