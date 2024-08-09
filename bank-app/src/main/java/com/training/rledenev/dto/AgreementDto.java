package com.training.rledenev.dto;

import com.training.rledenev.entity.enums.CurrencyCode;
import com.training.rledenev.entity.enums.ProductType;
import com.training.rledenev.entity.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgreementDto {
    private Long id;
    private BigDecimal sum;
    private ProductType productType;
    private CurrencyCode currencyCode;
    private Status status;
    private BigDecimal interestRate;
    private Integer periodMonths;
    private String productName;
}
