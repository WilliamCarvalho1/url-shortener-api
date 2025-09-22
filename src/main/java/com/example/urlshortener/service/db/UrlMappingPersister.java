package com.example.urlshortener.service.db;

import com.example.urlshortener.model.UrlMapping;

public interface UrlMappingPersister {
    UrlMapping saveFromResponse(UrlMapping urlMapping);
}
