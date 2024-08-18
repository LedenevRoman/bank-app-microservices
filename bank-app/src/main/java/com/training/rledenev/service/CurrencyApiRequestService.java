package com.training.rledenev.service;

import com.training.rledenev.enums.CurrencyCode;
import org.json.JSONObject;

import java.io.IOException;

public interface CurrencyApiRequestService {

    JSONObject getCurrencyJsonObject(CurrencyCode currency) throws IOException, InterruptedException;
}
