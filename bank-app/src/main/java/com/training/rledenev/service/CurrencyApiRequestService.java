package com.training.rledenev.service;

import com.training.rledenev.entity.enums.CurrencyCode;
import org.json.JSONObject;

import java.io.IOException;

public interface CurrencyApiRequestService {

    JSONObject getCurrencyJsonObject(CurrencyCode currency) throws IOException, InterruptedException;
}
