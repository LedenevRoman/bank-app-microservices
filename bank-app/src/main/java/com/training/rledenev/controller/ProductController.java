package com.training.rledenev.controller;

import com.training.rledenev.dto.ProductDto;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all-active")
    public List<ProductDto> getAllActiveProductDtos() {
        return productService.getAllActiveProductDtos();
    }

    @GetMapping("/all-active/{type}")
    public List<ProductDto> getAllActiveProductDtos(@PathVariable(name = "type") ProductType productType) {
        return productService.getActiveProductsWithType(productType);
    }

    @GetMapping("/suitable")
    public ProductDto getSuitableProduct(@RequestParam ProductType productType,
                                         @RequestParam(defaultValue = "0") BigDecimal amount,
                                         @RequestParam CurrencyCode currencyCode) {
        return productService.getSuitableProduct(productType, amount, currencyCode);
    }
}
