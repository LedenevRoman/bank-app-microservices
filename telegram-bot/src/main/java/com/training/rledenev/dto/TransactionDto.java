package com.training.rledenev.dto;

import lombok.Data;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionDto {
    private String debitAccountNumber;
    private String creditAccountNumber;
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    private String currencyCode;
    private Double debitBalanceDifference;
    private Double creditBalanceDifference;
    private String type;
    private String description;
    private Date createdAt;
}
