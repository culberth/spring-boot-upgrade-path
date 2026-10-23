package com.example.catalog.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 17)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private LocalDate publishedOn;

    @Column(nullable = false)
    private Instant createdAt;

    protected Book() {
    }

    public Book(String isbn, String title, String author, BigDecimal price, LocalDate publishedOn) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.publishedOn = publishedOn;
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() {
        return this.id;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public LocalDate getPublishedOn() {
        return this.publishedOn;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

}
