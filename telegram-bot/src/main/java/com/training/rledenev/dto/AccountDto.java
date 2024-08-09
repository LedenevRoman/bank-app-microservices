package com.training.rledenev.dto;

import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AccountDto {
    private String number;
    private String ownerFullName;
    private String managerFullName;
    private String productName;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate paymentTerm;
    private ProductType productType;
    private Status status;
    private BigDecimal balance;
    private CurrencyCode currencyCode;
    private String currencyName;
}
