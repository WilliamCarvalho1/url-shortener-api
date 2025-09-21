package com.example.urlshortener.exception;

public class UrlShorteningClientException extends RuntimeException {
    public UrlShorteningClientException(String message, Throwable cause) {
        super(message, cause);
    }
}