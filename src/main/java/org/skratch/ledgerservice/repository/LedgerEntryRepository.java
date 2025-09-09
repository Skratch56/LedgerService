package org.skratch.ledgerservice.repository;

import org.skratch.ledgerservice.model.Account;
import org.skratch.ledgerservice.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    Optional<LedgerEntry> findByTransferId(String transferId);
}
