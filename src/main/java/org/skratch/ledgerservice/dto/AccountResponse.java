package org.skratch.ledgerservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private BigDecimal balance;
    private Long version;
    private LocalDateTime createdAt;
}
