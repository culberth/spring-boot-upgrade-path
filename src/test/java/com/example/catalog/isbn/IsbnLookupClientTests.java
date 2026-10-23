package com.example.catalog.isbn;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.example.catalog.config.CatalogProperties;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Runs the client against a real in-process HTTP server. {@code MockRestServiceServer}
 * cannot be used here: it works by replacing the builder's request factory, and this
 * client sets its own request factory to apply per-client timeouts.
 */
class IsbnLookupClientTests {

    private HttpServer server;

    private IsbnLookupClient client;

    @BeforeEach
    void startServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        this.server.createContext("/isbn/", this::handle);
        this.server.start();
        URI baseUrl = URI.create("http://localhost:" + this.server.getAddress().getPort());
        CatalogProperties properties = new CatalogProperties(
                new CatalogProperties.IsbnLookup(baseUrl, Duration.ofSeconds(1), Duration.ofMillis(500)),
                new CatalogProperties.Warehouse(URI.create("http://localhost:1")));
        this.client = new IsbnLookupClient(RestClient.builder(), properties);
    }

    @AfterEach
    void stopServer() {
        this.server.stop(0);
    }

    @Test
    void returnsRecordForKnownIsbn() {
        assertThat(this.client.lookup("9780134685991"))
            .hasValueSatisfying(record -> assertThat(record.title()).isEqualTo("Effective Java"));
    }

    @Test
    void returnsEmptyForUnknownIsbn() {
        assertThat(this.client.lookup("9780000000000")).isEmpty();
    }

    @Test
    void appliesConfiguredReadTimeout() {
        assertThatExceptionOfType(ResourceAccessException.class).isThrownBy(() -> this.client.lookup("slow"));
    }

    private void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestURI().getPath()) {
            case "/isbn/9780134685991" -> respond(exchange, 200, """
                    {"isbn": "9780134685991", "title": "Effective Java", "author": "Joshua Bloch",
                     "publisher": "Addison-Wesley"}
                    """);
            case "/isbn/slow" -> {
                sleep(Duration.ofSeconds(2));
                respond(exchange, 200, "{}");
            }
            default -> respond(exchange, 404, "");
        }
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, (bytes.length > 0) ? bytes.length : -1);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
