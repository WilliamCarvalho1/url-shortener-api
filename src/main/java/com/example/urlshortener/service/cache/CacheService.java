package com.example.urlshortener.service.cache;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {
    void set(String key, String value, Duration ttl);

    Optional<String> get(String key);
}