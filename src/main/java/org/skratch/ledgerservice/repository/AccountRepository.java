package org.skratch.ledgerservice.repository;

import org.skratch.ledgerservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
