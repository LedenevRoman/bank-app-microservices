package com.training.rledenev.service.impl;

import com.training.rledenev.entity.enums.CurrencyCode;
import com.training.rledenev.exception.RequestApiException;
import com.training.rledenev.service.CurrencyApiRequestService;
import com.training.rledenev.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class CurrencyServiceNBP implements CurrencyService {
    private final CurrencyApiRequestService currencyApiRequestService;

    @Override
    public BigDecimal getRateOfCurrency(CurrencyCode currencyCode) {
        if (CurrencyCode.PLN == currencyCode) {
            return BigDecimal.valueOf(1);
        }
        JSONObject currencyJson;
        try {
            currencyJson = currencyApiRequestService.getCurrencyJsonObject(currencyCode);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            throw new RequestApiException(e.getMessage());
        }
        JSONObject subObject = currencyJson.getJSONArray("rates").getJSONObject(0);
        return BigDecimal.valueOf(subObject.getDouble("mid"));
    }
}
