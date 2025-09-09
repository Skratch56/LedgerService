package org.skratch.ledgerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.skratch.ledgerservice.dto.LedgerTransferRequest;
import org.skratch.ledgerservice.dto.LedgerTransferResponse;
import org.skratch.ledgerservice.model.LedgerEntry;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LedgerTransferMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "type", constant = "DEBIT")
    LedgerEntry toDebitEntity(LedgerTransferRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "type", constant = "CREDIT")
    LedgerEntry toCreditEntity(LedgerTransferRequest dto);

    default LedgerTransferResponse toLedgerTransferResponse(String transferId, boolean success, String message) {
        return LedgerTransferResponse.builder()
                .transferId(transferId)
                .success(success)
                .message(message)
                .build();
    }
}
