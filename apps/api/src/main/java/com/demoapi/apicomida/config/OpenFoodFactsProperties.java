package com.demoapi.apicomida.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.external.openfoodfacts")
public record OpenFoodFactsProperties(
        String baseUrl,
        int pageSize,
        int timeoutSeconds
) {
}
