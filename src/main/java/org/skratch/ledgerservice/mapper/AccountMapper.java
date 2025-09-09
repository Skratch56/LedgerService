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
    @Mapping(target = "version", constant = "0")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Account toEntity(CreateAccountRequest dto);

    AccountResponse toAccountResponse(Account account);
}
