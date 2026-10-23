package com.example.catalog.config;

import java.net.URI;
import java.time.Duration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("catalog")
@Validated
public record CatalogProperties(@NotNull @Valid IsbnLookup isbnLookup, @NotNull @Valid Warehouse warehouse) {

    public record IsbnLookup(@NotNull URI baseUrl, @DefaultValue("2s") Duration connectTimeout,
            @DefaultValue("5s") Duration readTimeout) {
    }

    public record Warehouse(@NotNull URI baseUrl) {
    }

}
