package com.pearchCash.payments.exceptions;

public class UsernameExistsException extends IllegalArgumentException {
    public UsernameExistsException(String message) {
        super(message);
    }
}
