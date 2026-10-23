package com.example.catalog.inventory;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.catalog.book.BookResponse;
import com.example.catalog.book.BookService;
import com.example.catalog.config.SecurityConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityController.class)
@Import(SecurityConfig.class)
class AvailabilityControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private WarehouseClient warehouseClient;

    @Test
    void reportsStockFromWarehouse() throws Exception {
        given(this.bookService.findById(1L)).willReturn(new BookResponse(1L, "9780134685991", "Effective Java",
                "Joshua Bloch", new BigDecimal("45.99"), null, Instant.now()));
        given(this.warehouseClient.stockLevel("9780134685991")).willReturn(new StockLevel("9780134685991", 0));

        this.mvc.perform(get("/api/books/1/availability"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available").value(0))
            .andExpect(jsonPath("$.inStock").value(false));
    }

}
