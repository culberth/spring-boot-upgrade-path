package com.example.catalog.notification;

import java.util.concurrent.atomic.AtomicLong;

import com.example.catalog.book.BookCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Sends a notification when a book is added, off the request thread.
 */
@Component
public class BookNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(BookNotificationListener.class);

    private final AtomicLong sent = new AtomicLong();

    @Async("taskExecutor")
    @EventListener
    public void onBookCreated(BookCreatedEvent event) {
        logger.info("New book in the catalog: {} ({})", event.title(), event.isbn());
        this.sent.incrementAndGet();
    }

    public long sentCount() {
        return this.sent.get();
    }

}
