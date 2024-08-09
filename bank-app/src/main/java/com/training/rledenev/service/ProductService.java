package com.training.rledenev.service;

import com.training.rledenev.dto.ProductDto;
import com.training.rledenev.entity.enums.CurrencyCode;
import com.training.rledenev.entity.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<ProductDto> getAllActiveProductDtos();

    List<ProductDto> getActiveProductsWithType(ProductType productType);

    ProductDto getSuitableProduct(ProductType productType, BigDecimal amount, CurrencyCode currencyCode);
}
