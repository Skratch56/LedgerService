package org.skratch.ledgerservice.service;

import org.skratch.ledgerservice.dto.AccountResponse;
import org.skratch.ledgerservice.dto.CreateAccountRequest;

public interface AccountService {
    AccountResponse createAccount(CreateAccountRequest request);
    AccountResponse getAccountById(Long id);
}
