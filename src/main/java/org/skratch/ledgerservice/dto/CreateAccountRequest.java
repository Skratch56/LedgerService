package org.skratch.ledgerservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    private BigDecimal initialBalance;
}
