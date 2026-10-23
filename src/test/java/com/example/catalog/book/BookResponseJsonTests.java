package com.example.catalog.book;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.example.catalog.config.JacksonConfig;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.Jackson2Tester;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Golden-file contract test for the JSON clients see. It compares the exact string, so it
 * also catches changes in property order or date format.
 * <p>
 * Written years ago with JUnit 4 and never converted.
 */
@RunWith(SpringRunner.class)
// Temporary: spring-boot-starter-test-classic brings the Gson and JSON-B tester
// auto-configurations without those libraries
@JsonTest(properties = "spring.autoconfigure.exclude="
        + "org.springframework.boot.gson.autoconfigure.GsonTesterTestAutoConfiguration,"
        + "org.springframework.boot.jsonb.autoconfigure.JsonbTesterTestAutoConfiguration")
@Import(JacksonConfig.class)
public class BookResponseJsonTests {

    @Autowired
    private Jackson2Tester<BookResponse> json;

    @Test
    public void serializesToContract() throws IOException {
        String expected = new ClassPathResource("json/book-response.json").getContentAsString(StandardCharsets.UTF_8)
            .strip();
        assertThat(this.json.write(BookControllerTests.effectiveJava()).getJson()).isEqualTo(expected);
    }

}
