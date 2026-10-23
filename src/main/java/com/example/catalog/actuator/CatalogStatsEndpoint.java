package com.example.catalog.actuator;

import java.util.Map;

import com.example.catalog.book.BookService;
import com.example.catalog.notification.BookNotificationListener;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Custom actuator endpoint exposing catalog statistics at {@code /actuator/catalogstats}.
 */
@Component
@RestControllerEndpoint(id = "catalogstats")
public class CatalogStatsEndpoint {

    private final BookService bookService;

    private final BookNotificationListener notifications;

    public CatalogStatsEndpoint(BookService bookService, BookNotificationListener notifications) {
        this.bookService = bookService;
        this.notifications = notifications;
    }

    @GetMapping
    public Map<String, Long> stats() {
        return Map.of("books", this.bookService.count(), "notificationsSent", this.notifications.sentCount());
    }

}
