package com.example.catalog.inventory;

import com.example.catalog.config.CatalogProperties;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Reads stock levels from the warehouse service using {@link RestClient}, the fluent
 * synchronous client introduced in Spring Framework 6.1.
 */
@Component
public class WarehouseClient {

    private final RestClient restClient;

    public WarehouseClient(RestClient.Builder builder, CatalogProperties properties) {
        this.restClient = builder.baseUrl(properties.warehouse().baseUrl().toString()).build();
    }

    public StockLevel stockLevel(String isbn) {
        return this.restClient.get().uri("/stock/{isbn}", isbn).retrieve().body(StockLevel.class);
    }

}
