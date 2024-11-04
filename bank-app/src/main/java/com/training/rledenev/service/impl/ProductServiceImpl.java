package com.training.rledenev.service.impl;

import com.training.rledenev.dto.ProductDto;
import com.training.rledenev.entity.Product;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.exception.ProductNotFoundException;
import com.training.rledenev.mapper.ProductMapper;
import com.training.rledenev.repository.ProductRepository;
import com.training.rledenev.service.CurrencyService;
import com.training.rledenev.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CurrencyService currencyService;

    @Transactional(readOnly = true)
    @Override
    public List<ProductDto> getAllActiveProductDtos() {
        return productMapper.mapToListDto(productRepository.findAllActiveProducts());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDto> getActiveProductsWithType(ProductType productType) {
        return productMapper.mapToListDto(productRepository
                .findAllActiveProductsWithType(productType));
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDto getSuitableProduct(ProductType productType, BigDecimal amount, CurrencyCode currencyCode) {
        Product product;
        if (productType == ProductType.DEBIT_CARD ||
                productType == ProductType.CREDIT_CARD) {
            product = productRepository.getCardProduct(productType.toString())
                    .orElseThrow(() -> new ProductNotFoundException("No product type"));
        } else {
            BigDecimal rate = currencyService.getRateOfCurrency(currencyCode);
            BigDecimal convertedAmount = amount.multiply(rate);
            product = productRepository.getProductByTypeSumAndPeriod(
                    productType.toString(),
                    convertedAmount.doubleValue()
            ).orElseThrow(() -> new ProductNotFoundException("Amount or period is out of limit"));
        }
        return productMapper.mapToDto(product);
    }
}
