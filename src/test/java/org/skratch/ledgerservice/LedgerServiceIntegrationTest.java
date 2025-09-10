package org.skratch.ledgerservice;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.skratch.ledgerservice.dto.LedgerTransferRequest;
import org.skratch.ledgerservice.dto.LedgerTransferResponse;
import org.skratch.ledgerservice.model.Account;
import org.skratch.ledgerservice.repository.AccountRepository;
import org.skratch.ledgerservice.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LedgerServiceIntegrationTest {

    @Autowired private LedgerService ledgerService;
    @Autowired private AccountRepository accountRepository;

    private Account from;
    private Account to;

    @BeforeEach
    void setup() {
        from = new Account();
        from.setBalance(BigDecimal.valueOf(500));
        from.setVersion(0L);
        from = accountRepository.save(from);

        to = new Account();
        to.setBalance(BigDecimal.valueOf(200));
        to.setVersion(0L);
        to = accountRepository.save(to);

        accountRepository.flush();

    }

    @Test
    void happyPathTransfer() {
        LedgerTransferRequest req = LedgerTransferRequest.builder().transferId("tx-happy").fromAccountId(from.getId()).toAccountId(to.getId()).amount(BigDecimal.valueOf(100)).build();

        LedgerTransferResponse resp = ledgerService.applyTransfer(req);

        assertThat(resp.isSuccess()).isTrue();
        assertThat(accountRepository.findById(from.getId()).get().getBalance())
                .isEqualByComparingTo("400");
        assertThat(accountRepository.findById(to.getId()).get().getBalance())
                .isEqualByComparingTo("300");
    }


    @Test
    void idempotencyShouldReturnSameResult() {
        LedgerTransferRequest req = LedgerTransferRequest.builder().transferId("tx-idem").fromAccountId(from.getId()).toAccountId(to.getId()).amount(BigDecimal.valueOf(50)).build();

        LedgerTransferResponse first = ledgerService.applyTransfer(req);
        LedgerTransferResponse second = ledgerService.applyTransfer(req);

        assertThat(second.getMessage()).contains("Duplicate transferId");
    }
}
