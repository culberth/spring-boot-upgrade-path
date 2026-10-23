package com.example.catalog.isbn;

import java.util.Optional;

import com.example.catalog.config.CatalogProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Looks up book metadata from the external ISBN service.
 */
@Component
public class IsbnLookupClient {

    private final RestTemplate restTemplate;

    public IsbnLookupClient(RestTemplateBuilder builder, CatalogProperties properties) {
        CatalogProperties.IsbnLookup isbnLookup = properties.isbnLookup();
        this.restTemplate = builder.rootUri(isbnLookup.baseUrl().toString())
            .connectTimeout(isbnLookup.connectTimeout())
            .readTimeout(isbnLookup.readTimeout())
            .build();
    }

    public Optional<IsbnRecord> lookup(String isbn) {
        try {
            return Optional.ofNullable(this.restTemplate.getForObject("/isbn/{isbn}", IsbnRecord.class, isbn));
        }
        catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

}
