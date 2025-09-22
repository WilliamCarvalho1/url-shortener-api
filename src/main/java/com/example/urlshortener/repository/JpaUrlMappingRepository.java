package com.example.urlshortener.repository;

import com.example.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByCode(Long code);

    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}
