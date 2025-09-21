package com.example.urlshortener.exception;

public class UrlShorteningServiceException extends RuntimeException {
    public UrlShorteningServiceException(String message) {
        super(message);
    }

    public UrlShorteningServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}