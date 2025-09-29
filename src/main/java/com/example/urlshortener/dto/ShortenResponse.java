package com.example.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ShortenResponse {
    private Long code;
    private String shortUrl;
}
