package com.example.urlshortener.service.external;

import com.example.urlshortener.client.LinkResponse;

public interface ExternalShorteningService {
    LinkResponse callExternalShortener(String originalUrl);
}
