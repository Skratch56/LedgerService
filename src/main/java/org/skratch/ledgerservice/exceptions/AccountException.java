package org.skratch.ledgerservice.exceptions;

public class AccountException extends RuntimeException {
    public AccountException(String message) {
        super(message);
    }
}
