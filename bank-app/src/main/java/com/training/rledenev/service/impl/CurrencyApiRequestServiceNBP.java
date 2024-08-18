package com.training.rledenev.service.impl;

import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.service.CurrencyApiRequestService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CurrencyApiRequestServiceNBP implements CurrencyApiRequestService {
    private static final String NATIONAL_BANK_POLAND_API_URL = "http://api.nbp.pl/api/exchangerates/rates/A/";

    @Override
    public JSONObject getCurrencyJsonObject(CurrencyCode currency) throws IOException, InterruptedException {
        String apiUrl = NATIONAL_BANK_POLAND_API_URL + currency;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        }
    }
}
