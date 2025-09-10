package org.skratch.ledgerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.skratch.ledgerservice.dto.AccountResponse;
import org.skratch.ledgerservice.dto.CreateAccountRequest;
import org.skratch.ledgerservice.model.Account;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", constant = "0l")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "balance", source = "initialBalance")
    Account toEntity(CreateAccountRequest dto);

    AccountResponse toAccountResponse(Account account);
}
