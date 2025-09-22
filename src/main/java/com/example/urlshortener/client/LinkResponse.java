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
    @JsonProperty("id")
    private Long code;
    @JsonProperty("url")
    private String originalUrl;
    @JsonProperty("full_url")
    private String shortUrl;
}
