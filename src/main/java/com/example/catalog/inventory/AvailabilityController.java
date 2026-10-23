package com.example.catalog.inventory;

import com.example.catalog.book.BookResponse;
import com.example.catalog.book.BookService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AvailabilityController {

    private final BookService bookService;

    private final WarehouseClient warehouseClient;

    public AvailabilityController(BookService bookService, WarehouseClient warehouseClient) {
        this.bookService = bookService;
        this.warehouseClient = warehouseClient;
    }

    @GetMapping("/api/books/{id}/availability")
    public Availability availability(@PathVariable Long id) {
        BookResponse book = this.bookService.findById(id);
        StockLevel stock = this.warehouseClient.stockLevel(book.isbn());
        return new Availability(book.id(), book.isbn(), stock.available(), stock.available() > 0);
    }

    public record Availability(Long bookId, String isbn, int available, boolean inStock) {
    }

}
