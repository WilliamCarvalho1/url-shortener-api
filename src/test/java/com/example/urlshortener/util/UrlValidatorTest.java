package com.example.urlshortener.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UrlValidatorTest {

    @Test
    void validateUrlValidHttpUrl() {
        assertDoesNotThrow(() -> UrlValidator.validateUrl("http://example.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "example.com",
            "http:///",
            "ftp://example.com",
            "http://exa mple.com"
    })
    void validateUrlMissingSchemeThrowsIllegalArgumentException(String url) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> UrlValidator.validateUrl(url)
        );
        assertTrue(ex.getMessage().contains("Invalid URL"));
    }

    @Test
    void validateUrlThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> UrlValidator.validateUrl(null));
    }
}