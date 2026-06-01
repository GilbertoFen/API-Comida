package com.demoapi.apicomida.services;

import com.demoapi.apicomida.config.OpenFoodFactsProperties;
import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.OpenFoodFactsProductResponse;
import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.ProductByBarcodeResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenFoodFactsClient {

    private static final Logger log = LoggerFactory.getLogger(OpenFoodFactsClient.class);

    private final RestClient restClient;
    private final OpenFoodFactsProperties properties;
    private final ObjectMapper objectMapper;

    public OpenFoodFactsClient(
            RestClient openFoodFactsRestClient,
            OpenFoodFactsProperties properties,
            ObjectMapper objectMapper
    ) {
        this.restClient = openFoodFactsRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public Optional<OpenFoodFactsProductResponse> findByBarcode(String barcode) {
        try {
            JsonNode body = restClient.get()
                    .uri("/api/v2/product/{barcode}.json", barcode)
                    .retrieve()
                    .body(JsonNode.class);

            if (body == null) {
                return Optional.empty();
            }

            ProductByBarcodeResponse response = objectMapper.treeToValue(body, ProductByBarcodeResponse.class);
            if (response == null || response.status() == null || response.status() != 1 || response.product() == null) {
                return Optional.empty();
            }

            return Optional.of(mergeRawData(response.product(), body.path("product")));
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Open Food Facts is unavailable", exception);
        } catch (Exception exception) {
            log.warn("Could not parse Open Food Facts barcode response for {}", barcode, exception);
            return Optional.empty();
        }
    }

    public List<OpenFoodFactsProductResponse> search(String query) {
        try {
            JsonNode body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi/search.pl")
                            .queryParam("search_terms", query)
                            .queryParam("search_simple", 1)
                            .queryParam("action", "process")
                            .queryParam("json", 1)
                            .queryParam("page_size", properties.pageSize())
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            if (body == null) {
                return Collections.emptyList();
            }

            JsonNode productsNode = body.path("products");
            if (!productsNode.isArray()) {
                return Collections.emptyList();
            }

            return toNodeList(productsNode).stream()
                    .map(this::parseProduct)
                    .filter(product -> product != null)
                    .toList();
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Open Food Facts is unavailable", exception);
        } catch (Exception exception) {
            log.warn("Could not parse Open Food Facts search response for {}", query, exception);
            return Collections.emptyList();
        }
    }

    private OpenFoodFactsProductResponse mergeRawData(OpenFoodFactsProductResponse product, JsonNode rawData) {
        return new OpenFoodFactsProductResponse(
                product.code(),
                product.productName(),
                product.brands(),
                product.categories(),
                product.nutriments(),
                rawData
        );
    }

    private OpenFoodFactsProductResponse parseProduct(JsonNode node) {
        try {
            OpenFoodFactsProductResponse product = objectMapper.treeToValue(node, OpenFoodFactsProductResponse.class);
            if (product == null) {
                return null;
            }
            return mergeRawData(product, node);
        } catch (Exception exception) {
            log.warn("Could not parse Open Food Facts product node", exception);
            return null;
        }
    }

    private List<JsonNode> toNodeList(JsonNode arrayNode) {
        List<JsonNode> nodes = new ArrayList<>();
        arrayNode.forEach(nodes::add);
        return nodes;
    }
}
