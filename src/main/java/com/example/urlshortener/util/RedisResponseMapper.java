package com.example.urlshortener.util;

import com.example.urlshortener.model.UrlMapping;

public class RedisResponseMapper {

    private RedisResponseMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final String SEP = "_";

    public static String concatenateCodeAndShortUrl(UrlMapping mapping) {
        return mapping.getCode() + SEP + mapping.getShortUrl();
    }

    public static UrlMapping deconcatenateCodeAndShortUrl(String stored) {
        int idx = stored.indexOf(SEP);
        Long code = Long.valueOf(stored.substring(0, idx));
        String shortUrl = stored.substring(idx + 1);

        return UrlMapping.builder()
                .code(code)
                .shortUrl(shortUrl)
                .build();
    }
}
