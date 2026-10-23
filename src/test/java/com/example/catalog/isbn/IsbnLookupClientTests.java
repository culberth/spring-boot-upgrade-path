package com.example.catalog.isbn;

import com.example.catalog.config.CatalogProperties;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(IsbnLookupClient.class)
class IsbnLookupClientTests {

    @Autowired
    private IsbnLookupClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void returnsRecordForKnownIsbn() {
        this.server.expect(requestTo("/isbn/9780134685991"))
            .andRespond(withSuccess("""
                    {"isbn": "9780134685991", "title": "Effective Java", "author": "Joshua Bloch",
                     "publisher": "Addison-Wesley"}
                    """, MediaType.APPLICATION_JSON));

        assertThat(this.client.lookup("9780134685991"))
            .hasValueSatisfying(record -> assertThat(record.title()).isEqualTo("Effective Java"));
    }

    @Test
    void returnsEmptyForUnknownIsbn() {
        this.server.expect(requestTo("/isbn/9780000000000")).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(this.client.lookup("9780000000000")).isEmpty();
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EnableConfigurationProperties(CatalogProperties.class)
    static class PropertiesConfiguration {

    }

}
