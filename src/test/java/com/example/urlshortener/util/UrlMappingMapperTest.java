package com.example.urlshortener.util;

import com.example.urlshortener.api.ShortenResponse;
import com.example.urlshortener.client.LinkResponse;
import com.example.urlshortener.model.UrlMapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlMappingMapperTest {

    @Test
    void linkResponseToUrlMappingMapperMapsFieldsCorrectly() {
        LinkResponse linkResponse = LinkResponse.builder()
                .code(123L)
                .shortUrl("http://sho.rt/abc")
                .build();

        UrlMapping mapping = UrlMappingMapper.linkResponseToUrlMappingMapper(linkResponse);

        assertEquals(123L, mapping.getCode());
        assertEquals("http://sho.rt/abc", mapping.getShortUrl());
    }

    @Test
    void linkResponseToShortenResponseMapperMapsFieldsCorrectly() {
        LinkResponse linkResponse = LinkResponse.builder()
                .code(456L)
                .shortUrl("http://sho.rt/xyz")
                .build();

        ShortenResponse response = UrlMappingMapper.linkResponseToShortenResponseMapper(linkResponse);

        assertEquals(456L, response.getCode());
        assertEquals("http://sho.rt/xyz", response.getShortUrl());
    }

    @Test
    void urlMappingToShortenResponseMapperMapsFieldsCorrectly() {
        UrlMapping mapping = UrlMapping.builder()
                .code(789L)
                .shortUrl("http://sho.rt/qwe")
                .build();

        ShortenResponse response = UrlMappingMapper.urlMappingToShortenResponseMapper(mapping);

        assertEquals(789L, response.getCode());
        assertEquals("http://sho.rt/qwe", response.getShortUrl());
    }
}