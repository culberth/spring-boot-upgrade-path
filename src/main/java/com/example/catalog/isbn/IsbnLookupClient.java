package com.example.catalog.isbn;

import java.util.Optional;

import com.example.catalog.config.CatalogProperties;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Looks up book metadata from the external ISBN service.
 */
@Component
public class IsbnLookupClient {

    private final RestClient restClient;

    public IsbnLookupClient(RestClient.Builder builder, CatalogProperties properties) {
        CatalogProperties.IsbnLookup isbnLookup = properties.isbnLookup();
        HttpClientSettings settings = HttpClientSettings.defaults()
            .withTimeouts(isbnLookup.connectTimeout(), isbnLookup.readTimeout());
        this.restClient = builder.baseUrl(isbnLookup.baseUrl().toString())
            .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
            .build();
    }

    public Optional<IsbnRecord> lookup(String isbn) {
        try {
            return Optional.ofNullable(this.restClient.get().uri("/isbn/{isbn}", isbn).retrieve().body(IsbnRecord.class));
        }
        catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

}
