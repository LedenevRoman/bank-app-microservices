package com.training.rledenev.mapper;

import com.training.rledenev.dto.TransactionDto;
import com.training.rledenev.entity.Transaction;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Named("toTransactionDto")
    @Mapping(source = "debitAccount.number", target = "debitAccountNumber")
    @Mapping(source = "creditAccount.number", target = "creditAccountNumber")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "mapToDate")
    TransactionDto mapToDto(Transaction transaction);

    Transaction mapToEntity(TransactionDto transactionDto);

    @IterableMapping(qualifiedByName = "toTransactionDto")
    List<TransactionDto> mapToListDto(List<Transaction> transactions);

    @Named("mapToDate")
    default Date mapToDate(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }
}
