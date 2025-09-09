package org.skratch.ledgerservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.skratch.ledgerservice.dto.LedgerTransferRequest;
import org.skratch.ledgerservice.dto.LedgerTransferResponse;
import org.skratch.ledgerservice.mapper.LedgerTransferMapper;
import org.skratch.ledgerservice.model.Account;
import org.skratch.ledgerservice.model.LedgerEntry;
import org.skratch.ledgerservice.repository.AccountRepository;
import org.skratch.ledgerservice.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerTransferMapper ledgerTransferMapper;

    @Override
    @Transactional
    public LedgerTransferResponse applyTransfer(LedgerTransferRequest request) {
        Optional<LedgerEntry> existing = ledgerEntryRepository.findByTransferId(request.getTransferId());
        if (existing.isPresent()) {
            return ledgerTransferMapper.toLedgerTransferResponse(
                    request.getTransferId(), true, "Duplicate transferId – no changes applied");
        }

        Account from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("From account not found: " + request.getFromAccountId()));
        Account to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new IllegalArgumentException("To account not found: " + request.getToAccountId()));

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            return ledgerTransferMapper.toLedgerTransferResponse(
                    request.getTransferId(), false, "Insufficient funds in account " + from.getId());
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);


        LedgerEntry debitEntry = ledgerTransferMapper.toDebitEntity(request);
        debitEntry.setAccountId(from.getId());
        ledgerEntryRepository.save(debitEntry);

        LedgerEntry creditEntry = ledgerTransferMapper.toCreditEntity(request);
        creditEntry.setAccountId(to.getId());
        ledgerEntryRepository.save(creditEntry);

        return ledgerTransferMapper.toLedgerTransferResponse(
                request.getTransferId(), true, "Transfer applied successfully");
    }
}
