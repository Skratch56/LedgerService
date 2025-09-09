package org.skratch.ledgerservice.service;

import org.skratch.ledgerservice.dto.LedgerTransferRequest;
import org.skratch.ledgerservice.dto.LedgerTransferResponse;

public interface LedgerService {
    LedgerTransferResponse applyTransfer(LedgerTransferRequest request);
}
