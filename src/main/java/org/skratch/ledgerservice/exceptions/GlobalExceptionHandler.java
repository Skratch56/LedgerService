package org.skratch.ledgerservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LedgerException.class)
    public ResponseEntity<Object> handleExpenseTrackerException(LedgerException ex) {
        log.error("Error while processing Expenses {}", ex.getMessage());
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(ex.getMessage(), httpStatus);
    }

}
