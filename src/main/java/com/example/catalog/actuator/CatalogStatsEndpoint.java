package com.example.catalog.actuator;

import java.util.Map;

import com.example.catalog.book.BookService;
import com.example.catalog.notification.BookNotificationListener;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/**
 * Custom actuator endpoint exposing catalog statistics at {@code /actuator/catalogstats}.
 */
@Component
@Endpoint(id = "catalogstats")
public class CatalogStatsEndpoint {

    private final BookService bookService;

    private final BookNotificationListener notifications;

    public CatalogStatsEndpoint(BookService bookService, BookNotificationListener notifications) {
        this.bookService = bookService;
        this.notifications = notifications;
    }

    @ReadOperation
    public Map<String, Long> stats() {
        return Map.of("books", this.bookService.count(), "notificationsSent", this.notifications.sentCount());
    }

}
