package com.training.rledenev.dto;

import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionDto {
    private Long id;
    private String debitAccountNumber;
    private String creditAccountNumber;
    private BigDecimal amount;
    private CurrencyCode currencyCode;
    private BigDecimal debitBalanceDifference;
    private CurrencyCode debitCurrencyCode;
    private BigDecimal creditBalanceDifference;
    private CurrencyCode creditCurrencyCode;
    private TransactionType type;
    private String description;
    private Date createdAt;
}
