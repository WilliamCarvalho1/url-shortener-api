package com.example.urlshortener.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkResponse {
    private Long id;
    @JsonProperty("url")
    private String originalUrl;
    @JsonProperty("full_url")
    private String shortUrl;
}
