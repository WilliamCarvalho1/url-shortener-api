package com.example.urlshortener.util;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidator {

    private UrlValidator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || uri.getHost() == null ||
                    !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Invalid URL: missing scheme or host");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + e.getMessage(), e);
        }
    }
}
