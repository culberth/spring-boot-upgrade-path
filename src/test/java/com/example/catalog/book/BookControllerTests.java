package com.example.catalog.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.example.catalog.config.JacksonConfig;
import com.example.catalog.config.SecurityConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import({ SecurityConfig.class, JacksonConfig.class })
class BookControllerTests {

    private static final String VALID_REQUEST = """
            {"isbn": "9780134685991", "price": 45.99}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void listIsPublicAndSerializesPricesAsStrings() throws Exception {
        given(this.bookService.findAll()).willReturn(List.of(effectiveJava()));
        this.mvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].isbn").value("9780134685991"))
            .andExpect(jsonPath("$[0].price").value("45.99"));
    }

    @Test
    void missingBookReturnsProblemDetail() throws Exception {
        given(this.bookService.findById(42L)).willThrow(new BookNotFoundException(42L));
        this.mvc.perform(get("/api/books/42"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Book 42 not found"));
    }

    @Test
    void createRequiresAuthentication() throws Exception {
        this.mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createReturnsCreatedWithLocation() throws Exception {
        given(this.bookService.create(any())).willReturn(effectiveJava());
        this.mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/books/1"))
            .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    @WithMockUser
    void createRejectsInvalidIsbn() throws Exception {
        this.mvc
            .perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                .content("{\"isbn\": \"not-an-isbn\", \"price\": 10}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createReturnsUnprocessableContentWhenServiceRejectsBook() throws Exception {
        given(this.bookService.create(any())).willThrow(new InvalidBookException("duplicate"));
        this.mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
            .andExpect(status().is(422))
            .andExpect(jsonPath("$.detail").value("duplicate"));
    }

    static BookResponse effectiveJava() {
        return new BookResponse(1L, "9780134685991", "Effective Java", "Joshua Bloch", new BigDecimal("45.99"),
                LocalDate.of(2018, 1, 6), Instant.parse("2024-05-01T10:15:30Z"));
    }

}
