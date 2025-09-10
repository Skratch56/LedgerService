package org.skratch.ledgerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.skratch.ledgerservice.dto.LedgerTransferRequest;
import org.skratch.ledgerservice.dto.LedgerTransferResponse;
import org.skratch.ledgerservice.service.LedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "Endpoints for applying transfers")
public class LedgerController {

    private final LedgerService ledgerService;

    @Operation(summary = "Apply a transfer", description = "Atomically applies a transfer (debit/credit). Idempotent via transferId.")
    @PostMapping("/transfer")
    public ResponseEntity<LedgerTransferResponse> transfer(@RequestBody @Valid LedgerTransferRequest request) {
        LedgerTransferResponse response = ledgerService.applyTransfer(request);
        return ResponseEntity.ok(response);
    }
}
