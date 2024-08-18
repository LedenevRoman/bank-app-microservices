package com.training.rledenev.service;

import com.training.rledenev.enums.CurrencyCode;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal getRateOfCurrency(CurrencyCode currencyCode);
}
