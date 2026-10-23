package com.example.catalog.book;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTests {

    @Autowired
    private BookRepository repository;

    @Test
    void savesAndFindsByIsbn() {
        this.repository.save(new Book("9780134685991", "Effective Java", "Joshua Bloch", new BigDecimal("45.99"),
                LocalDate.of(2018, 1, 6)));

        assertThat(this.repository.findByIsbn("9780134685991")).hasValueSatisfying(book -> {
            assertThat(book.getId()).isNotNull();
            assertThat(book.getCreatedAt()).isNotNull();
            assertThat(book.getPrice()).isEqualByComparingTo("45.99");
        });
        assertThat(this.repository.existsByIsbn("9780134685991")).isTrue();
        assertThat(this.repository.existsByIsbn("9780000000000")).isFalse();
    }

}
