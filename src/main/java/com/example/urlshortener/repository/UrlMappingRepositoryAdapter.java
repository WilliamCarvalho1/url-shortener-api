package com.example.urlshortener.repository;

import com.example.urlshortener.model.UrlMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UrlMappingRepositoryAdapter implements UrlMappingRepositoryPort {

    private final JpaUrlMappingRepository jpaRepository;

    @Autowired
    public UrlMappingRepositoryAdapter(JpaUrlMappingRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(UrlMapping mapping) {
        jpaRepository.save(mapping);
    }

    @Override
    public Optional<UrlMapping> findByCode(Long code) {
        return jpaRepository.findByCode(code);
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrl(String originalUrl) {
        return jpaRepository.findByOriginalUrl(originalUrl);
    }
}
