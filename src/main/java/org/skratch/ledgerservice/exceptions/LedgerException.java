package org.skratch.ledgerservice.exceptions;

public class LedgerException extends RuntimeException {
    public LedgerException(String message) {
        super(message);
    }
}
