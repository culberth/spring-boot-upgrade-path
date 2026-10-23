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
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMetrics
@AutoConfigureRestTestClient
class CatalogApplicationTests {

    @Autowired
    private RestTestClient client;

    @Autowired
    private BookNotificationListener notifications;

    @MockitoBean
    private IsbnLookupClient isbnLookupClient;

    @Test
    void createsBookFromIsbnLookupAndNotifiesAsynchronously() {
        given(this.isbnLookupClient.lookup("9781617294945")).willReturn(Optional
            .of(new IsbnRecord("9781617294945", "Spring in Action", "Craig Walls", "Manning")));
        long notificationsBefore = this.notifications.sentCount();

        String location = this.client.post()
            .uri("/api/books")
            .headers(headers -> headers.setBasicAuth("admin", "admin"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Map.of("isbn", "9781617294945", "price", "39.99"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.title").isEqualTo("Spring in Action")
            .jsonPath("$.price").isEqualTo("39.99")
            .returnResult()
            .getResponseHeaders().getLocation().toString();

        this.client.get()
            .uri(location)
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.author").isEqualTo("Craig Walls");

        await().atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> assertThat(this.notifications.sentCount()).isGreaterThan(notificationsBefore));
    }

    @Test
    void anonymousClientsSeeErrorStatusNotUnauthorized() {
        this.client.get().uri("/api/books/not-a-number").exchange().expectStatus().isBadRequest();
    }

    @Test
    void healthIsPublic() {
        this.client.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void customCatalogStatsEndpointRequiresAuthentication() {
        this.client.get().uri("/actuator/catalogstats").exchange().expectStatus().isUnauthorized();

        this.client.get()
            .uri("/actuator/catalogstats")
            .headers(headers -> headers.setBasicAuth("admin", "admin"))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.books").exists();
    }

    @Test
    void prometheusEndpointIsExposed() {
        this.client.get()
            .uri("/actuator/prometheus")
            .headers(headers -> headers.setBasicAuth("admin", "admin"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).value(body -> assertThat(body).contains("jvm_memory_used_bytes"));
    }

    @Test
    void unexposedEndpointsAreNotAvailable() {
        this.client.get()
            .uri("/actuator/env")
            .headers(headers -> headers.setBasicAuth("admin", "admin"))
            .exchange()
            .expectStatus().isNotFound();
    }

}
