package org.skratch.ledgerservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skratch.ledgerservice.exceptions.LedgerException;
import org.skratch.ledgerservice.mapper.LedgerTransferMapper;
import org.skratch.ledgerservice.model.Account;
import org.skratch.ledgerservice.model.LedgerEntry;
import org.skratch.ledgerservice.repository.AccountRepository;
import org.skratch.ledgerservice.repository.LedgerEntryRepository;
import org.skratch.ledgerservice.dto.LedgerTransferRequest;
import org.skratch.ledgerservice.dto.LedgerTransferResponse;
import org.skratch.ledgerservice.service.LedgerServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LedgerServiceApplicationTests {

    private LedgerServiceImpl ledgerService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    private LedgerTransferMapper ledgerTransferMapper = Mappers.getMapper(LedgerTransferMapper.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ledgerService = new LedgerServiceImpl(accountRepository, ledgerEntryRepository, ledgerTransferMapper);
    }

    @Test
    void shouldRejectDuplicateTransferId() {
        LedgerTransferRequest req = LedgerTransferRequest.builder()
                .transferId("tx-123").fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(50)).build();
        when(ledgerEntryRepository.findByTransferId("tx-123")).thenReturn(List.of(new LedgerEntry()));

        LedgerTransferResponse resp = ledgerService.applyTransfer(req);

        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getMessage()).contains("Duplicate transferId");
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldRejectOverdraft() {
        LedgerTransferRequest req = LedgerTransferRequest.builder()
                .transferId("tx-124").fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(52000)).build();
        Account from = new Account();
        from.setId(1L);
        from.setBalance(BigDecimal.valueOf(100));
        Account to = new Account();
        to.setId(2L);
        to.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

        LedgerException ex = assertThrows(
                LedgerException.class,
                () -> ledgerService.applyTransfer(req)
        );

        assertThat(ex.getMessage()).contains("Insufficient funds in account: 1");
        verify(accountRepository, never()).save(to);
    }

    @Test
    void shouldApplyTransferSuccessfully() {
        LedgerTransferRequest req = LedgerTransferRequest.builder()
                .transferId("tx-125").fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(50)).build();
        Account from = new Account();
        from.setId(1L);
        from.setBalance(BigDecimal.valueOf(100));
        Account to = new Account();
        to.setId(2L);
        to.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));
        when(ledgerEntryRepository.findByTransferId("tx-125")).thenReturn(List.of());

        LedgerTransferResponse resp = ledgerService.applyTransfer(req);

        assertThat(resp.isSuccess()).isTrue();
        assertThat(from.getBalance()).isEqualByComparingTo("50");
        assertThat(to.getBalance()).isEqualByComparingTo("100");
        verify(ledgerEntryRepository, times(2)).save(any(LedgerEntry.class));
    }

}
