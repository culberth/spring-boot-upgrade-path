package com.example.catalog.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BookResponse(Long id, String isbn, String title, String author, BigDecimal price,
        LocalDate publishedOn, Instant createdAt) {

    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPrice(),
                book.getPublishedOn(), book.getCreatedAt());
    }

}
