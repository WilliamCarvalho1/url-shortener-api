package com.example.urlshortener.util;

public class RedisResponseMapper {

    private RedisResponseMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Long getCodeFromRedisResponse(String input) {
        int idx = input.indexOf('_');
        return Long.parseLong(input.substring(0, idx));
    }

    public static String getShortUrlFromRedisResponse(String input) {
        int idx = input.indexOf('_');
        return input.substring(idx + 1);
    }
}
