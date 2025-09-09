package org.skratch.ledgerservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LedgerTransferRequest {
    private String transferId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
