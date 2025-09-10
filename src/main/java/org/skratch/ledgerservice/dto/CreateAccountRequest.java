package org.skratch.ledgerservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotNull(message = "Initial balance is required")
    @Min(value = 0, message = "Initial balance must be non-negative")
    private BigDecimal initialBalance;
}
