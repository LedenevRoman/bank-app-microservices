package com.training.rledenev.dto;

import com.training.rledenev.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private String name;
    private ProductType type;
    private Integer minLimit;
    private BigDecimal interestRate;
    private Integer periodMonths;
}
