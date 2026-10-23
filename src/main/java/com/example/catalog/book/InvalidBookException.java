package com.example.catalog.book;

public class InvalidBookException extends RuntimeException {

    public InvalidBookException(String message) {
        super(message);
    }

}
