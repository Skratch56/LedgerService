package org.skratch.ledgerservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skratch.ledgerservice.dto.LedgerTransferRequest;
import org.skratch.ledgerservice.dto.LedgerTransferResponse;
import org.skratch.ledgerservice.exceptions.LedgerException;
import org.skratch.ledgerservice.exceptions.ResourceNotFoundException;
import org.skratch.ledgerservice.mapper.LedgerTransferMapper;
import org.skratch.ledgerservice.model.Account;
import org.skratch.ledgerservice.model.LedgerEntry;
import org.skratch.ledgerservice.repository.AccountRepository;
import org.skratch.ledgerservice.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerTransferMapper ledgerTransferMapper;

    @Override
    @Transactional
    public LedgerTransferResponse applyTransfer(LedgerTransferRequest request) {
        log.info("Processing transferId={} fromAccountId={} toAccountId={} amount={}",
                request.getTransferId(), request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        List<LedgerEntry> existing = ledgerEntryRepository.findByTransferId(request.getTransferId());
        if (!existing.isEmpty()) {
            log.warn("Duplicate transfer detected: transferId={} already processed. Returning previous result.", request.getTransferId());
            return ledgerTransferMapper.toLedgerTransferResponse(
                    request.getTransferId(), true, "Duplicate transferId – no changes applied");
        }

        // ✅ Lock accounts (fail if not found)
        Account from = accountRepository.findByIdForUpdate(request.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("From account not found: " + request.getFromAccountId()));
        Account to = accountRepository.findByIdForUpdate(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("To account not found: " + request.getToAccountId()));

        // ✅ Prevent overdraft
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Insufficient funds: fromAccountId={} balance={} attemptedAmount={}",
                    from.getId(), from.getBalance(), request.getAmount());
            throw new LedgerException("Insufficient funds in account: " + request.getFromAccountId());
        }

        log.debug("Debiting {} from accountId={} and crediting to accountId={}",
                request.getAmount(), from.getId(), to.getId());
        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        // ✅ Insert DEBIT entry
        LedgerEntry debit = ledgerTransferMapper.toDebitEntity(request);
        debit.setTransferId(request.getTransferId());
        debit.setAccountId(from.getId());
        ledgerEntryRepository.save(debit);

        // ✅ Insert CREDIT entry
        LedgerEntry credit = ledgerTransferMapper.toCreditEntity(request);
        credit.setTransferId(request.getTransferId());
        credit.setAccountId(to.getId());
        ledgerEntryRepository.save(credit);

        log.info("TransferId={} applied successfully", request.getTransferId());

        return ledgerTransferMapper.toLedgerTransferResponse(
                request.getTransferId(), true, "Transfer applied successfully");
    }
}
