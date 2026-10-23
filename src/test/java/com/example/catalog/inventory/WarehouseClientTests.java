package com.example.catalog.inventory;

import com.example.catalog.config.CatalogProperties;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = WarehouseClient.class,
        properties = "catalog.warehouse.base-url=https://warehouse.example.com")
class WarehouseClientTests {

    @Autowired
    private WarehouseClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void readsStockLevel() {
        this.server.expect(requestTo("https://warehouse.example.com/stock/9780134685991"))
            .andRespond(withSuccess("{\"isbn\": \"9780134685991\", \"available\": 7}", MediaType.APPLICATION_JSON));

        assertThat(this.client.stockLevel("9780134685991").available()).isEqualTo(7);
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EnableConfigurationProperties(CatalogProperties.class)
    static class PropertiesConfiguration {

    }

}
