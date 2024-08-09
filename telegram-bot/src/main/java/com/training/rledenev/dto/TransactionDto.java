package com.training.rledenev.dto;

import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.TransactionType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionDto {
    private String debitAccountNumber;
    private String creditAccountNumber;
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    private CurrencyCode currencyCode;
    private BigDecimal debitBalanceDifference;
    private BigDecimal creditBalanceDifference;
    private TransactionType type;
    private String description;
    private Date createdAt;
}
