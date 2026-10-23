package com.example.catalog.book;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.example.catalog.config.JacksonConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Golden-file contract test for the JSON clients see. It compares the exact string, so it
 * also catches changes in property order or date format.
 */
@JsonTest
@Import(JacksonConfig.class)
class BookResponseJsonTests {

    @Autowired
    private JacksonTester<BookResponse> json;

    @Test
    void serializesToContract() throws IOException {
        String expected = new ClassPathResource("json/book-response.json").getContentAsString(StandardCharsets.UTF_8)
            .strip();
        assertThat(this.json.write(BookControllerTests.effectiveJava()).getJson()).isEqualTo(expected);
    }

}
