# URL Shortener API (Java / Spring Boot)

This repository contains an API for an URL shortener service.

Key features:
- REST API to shorten URLs and resolve them
- Spring Boot, JPA (H2 for dev), Actuator for health/metrics
- Validation and basic error handling
- Dockerfile and docker-compose skeleton included

## Endpoints
- `POST /api/v1/shorten` with JSON `{ "url": "<long-url>" }` -> returns `{ shortCode, shortUrl }`
- `GET /api/v1/resolve/{code}` -> returns the original URL (JSON or 404)

## Running locally
```bash
mvn spring-boot:run
```

## Access Open API
http://localhost:8080/swagger-ui.html

## Access Health endpoint
http://localhost:8080/actuator/health