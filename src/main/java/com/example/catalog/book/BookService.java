package com.example.catalog.book;

import java.util.List;

import com.example.catalog.isbn.IsbnLookupClient;
import com.example.catalog.isbn.IsbnRecord;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository repository;

    private final IsbnLookupClient isbnLookupClient;

    private final ApplicationEventPublisher events;

    public BookService(BookRepository repository, IsbnLookupClient isbnLookupClient, ApplicationEventPublisher events) {
        this.repository = repository;
        this.isbnLookupClient = isbnLookupClient;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return this.repository.findAll().stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return this.repository.findById(id).map(BookResponse::from).orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public BookResponse create(CreateBookRequest request) {
        if (this.repository.existsByIsbn(request.isbn())) {
            throw new InvalidBookException("A book with ISBN " + request.isbn() + " already exists");
        }
        String title = request.title();
        String author = request.author();
        if (isBlank(title) || isBlank(author)) {
            IsbnRecord record = this.isbnLookupClient.lookup(request.isbn())
                .orElseThrow(() -> new InvalidBookException(
                        "Title and author are required because ISBN " + request.isbn() + " is unknown"));
            title = isBlank(title) ? record.title() : title;
            author = isBlank(author) ? record.author() : author;
        }
        Book book = this.repository
            .save(new Book(request.isbn(), title, author, request.price(), request.publishedOn()));
        this.events.publishEvent(new BookCreatedEvent(book.getId(), book.getIsbn(), book.getTitle()));
        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!this.repository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        this.repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return this.repository.count();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
