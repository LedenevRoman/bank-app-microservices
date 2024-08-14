package com.training.rledenev.service.action;

import com.training.rledenev.service.action.impl.AccountsMessageHandlerService;
import com.training.rledenev.service.action.impl.AgreementMessageHandlerService;
import com.training.rledenev.service.action.impl.CurrencyRatesHandlerService;
import com.training.rledenev.service.action.impl.ProductMessageHandlerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.training.rledenev.util.BotUtils.*;

@Component
@RequiredArgsConstructor
public final class NameActionServiceMap {
    private static final Map<String, ActionMessageHandlerService> NAME_ACTION_MAP = new ConcurrentHashMap<>();

    private final AgreementMessageHandlerService agreementsMessageHandlerService;
    private final ProductMessageHandlerService productMessageHandlerService;
    private final CurrencyRatesHandlerService currencyRatesHandlerService;
    private final AccountsMessageHandlerService accountsMessageHandlerService;

    @PostConstruct
    private void init() {
        NAME_ACTION_MAP.put(MY_ACCOUNTS, accountsMessageHandlerService);
        NAME_ACTION_MAP.put(NEW_AGREEMENTS, agreementsMessageHandlerService);
        NAME_ACTION_MAP.put(PRODUCTS, productMessageHandlerService);
        NAME_ACTION_MAP.put(CURRENCY_RATES, currencyRatesHandlerService);
    }

    public static boolean containsKey(String message) {
        return NAME_ACTION_MAP.containsKey(message);
    }

    public static ActionMessageHandlerService get(String name) {
        return NAME_ACTION_MAP.get(name);
    }

}
