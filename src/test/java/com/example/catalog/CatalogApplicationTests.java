package com.example.catalog;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import com.example.catalog.isbn.IsbnLookupClient;
import com.example.catalog.isbn.IsbnRecord;
import com.example.catalog.notification.BookNotificationListener;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMetrics
@AutoConfigureTestRestTemplate
class CatalogApplicationTests {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private BookNotificationListener notifications;

    @MockitoBean
    private IsbnLookupClient isbnLookupClient;

    @Test
    @SuppressWarnings("rawtypes")
    void createsBookFromIsbnLookupAndNotifiesAsynchronously() {
        given(this.isbnLookupClient.lookup("9781617294945")).willReturn(Optional
            .of(new IsbnRecord("9781617294945", "Spring in Action", "Craig Walls", "Manning")));
        long notificationsBefore = this.notifications.sentCount();

        ResponseEntity<Map> created = this.rest.withBasicAuth("admin", "admin")
            .postForEntity("/api/books", Map.of("isbn", "9781617294945", "price", "39.99"), Map.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).containsEntry("title", "Spring in Action").containsEntry("price", "39.99");

        ResponseEntity<Map> fetched = this.rest.getForEntity(created.getHeaders().getLocation(), Map.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody()).containsEntry("author", "Craig Walls");

        await().atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> assertThat(this.notifications.sentCount()).isGreaterThan(notificationsBefore));
    }

    @Test
    void anonymousClientsSeeErrorStatusNotUnauthorized() {
        assertThat(this.rest.getForEntity("/api/books/not-a-number", String.class).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void healthIsPublic() {
        ResponseEntity<String> health = this.rest.getForEntity("/actuator/health", String.class);
        assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(health.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void customCatalogStatsEndpointRequiresAuthentication() {
        assertThat(this.rest.getForEntity("/actuator/catalogstats", String.class).getStatusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED);

        ResponseEntity<String> stats = this.rest.withBasicAuth("admin", "admin")
            .getForEntity("/actuator/catalogstats", String.class);
        assertThat(stats.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(stats.getBody()).contains("\"books\"");
    }

    @Test
    void prometheusEndpointIsExposed() {
        ResponseEntity<String> scrape = this.rest.withBasicAuth("admin", "admin")
            .getForEntity("/actuator/prometheus", String.class);
        assertThat(scrape.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(scrape.getBody()).contains("jvm_memory_used_bytes");
    }

    @Test
    void unexposedEndpointsAreNotAvailable() {
        assertThat(this.rest.withBasicAuth("admin", "admin").getForEntity("/actuator/env", String.class)
            .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
