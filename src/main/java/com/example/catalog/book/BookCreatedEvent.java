package com.example.catalog.book;

public record BookCreatedEvent(Long bookId, String isbn, String title) {
}
