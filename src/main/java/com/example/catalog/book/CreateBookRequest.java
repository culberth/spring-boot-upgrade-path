package com.example.catalog.book;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Request to add a book. Title and author are optional; when missing they are filled in
 * from the ISBN lookup service.
 */
public record CreateBookRequest(
        @NotBlank @Pattern(regexp = "^(97[89])?\\d{9}[\\dX]$", message = "must be a 10 or 13 digit ISBN") String isbn,
        String title,
        String author,
        @NotNull @Positive BigDecimal price,
        LocalDate publishedOn) {
}
