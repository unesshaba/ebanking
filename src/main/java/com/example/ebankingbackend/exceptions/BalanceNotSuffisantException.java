package com.example.ebankingbackend.exceptions;

public class BalanceNotSuffisantException extends RuntimeException {
    public BalanceNotSuffisantException(String message){
        super(message);
    }
}
