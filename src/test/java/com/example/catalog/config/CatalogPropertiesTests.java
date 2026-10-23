package com.example.catalog.config;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogPropertiesTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(PropertiesConfiguration.class)
        .withPropertyValues("catalog.warehouse.base-url=https://warehouse.example.com");

    @Test
    void bindsNestedIsbnLookupWithDefaults() {
        this.contextRunner.withPropertyValues("catalog.isbn-lookup.base-url=https://isbn.example.com")
            .run(context -> {
                assertThat(context).hasNotFailed();
                CatalogProperties.IsbnLookup isbnLookup = context.getBean(CatalogProperties.class).isbnLookup();
                assertThat(isbnLookup.connectTimeout()).isEqualTo(Duration.ofSeconds(2));
                assertThat(isbnLookup.readTimeout()).isEqualTo(Duration.ofSeconds(5));
            });
    }

    @Test
    void failsFastWhenNestedBaseUrlIsMissing() {
        this.contextRunner.withPropertyValues("catalog.isbn-lookup.read-timeout=10s")
            .run(context -> assertThat(context).hasFailed());
    }

    @EnableConfigurationProperties(CatalogProperties.class)
    static class PropertiesConfiguration {

    }

}
