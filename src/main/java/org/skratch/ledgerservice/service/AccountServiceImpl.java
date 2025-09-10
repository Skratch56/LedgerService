package org.skratch.ledgerservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skratch.ledgerservice.dto.AccountResponse;
import org.skratch.ledgerservice.dto.CreateAccountRequest;
import org.skratch.ledgerservice.exceptions.AccountException;
import org.skratch.ledgerservice.mapper.AccountMapper;
import org.skratch.ledgerservice.model.Account;
import org.skratch.ledgerservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = accountMapper.toEntity(request);
        Account saved = accountRepository.save(account);
        log.info("Creating new account with initialBalance={}", request.getInitialBalance());
        return accountMapper.toAccountResponse(saved);
    }

    @Override
    @Transactional
    public AccountResponse getAccountById(Long id) {
        log.info("Fetching account with id={}", id);
        return accountRepository.findById(id)
                .map(accountMapper::toAccountResponse)
                .orElseThrow(() -> new AccountException("Account not found: " + id));
    }
}
