package com.example.ebankingbackend.exceptions;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(String message ) {
        super(message);
    }
}
