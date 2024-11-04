package com.training.rledenev.enums;

import lombok.Getter;

@Getter
public enum CurrencyCode {
    PLN("Zloty", "zł"),
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    GBP("Pound Sterling", "£"),
    CHF("Swiss Franc", "CHF"),
    HUF("Forint", "Ft"),
    UAH("Hryvnia", "₴"),
    CZK("Czech Koruna", "Kč"),
    DKK("Danish Krone", "kr"),
    NOK("Norwegian Krone", "kr"),
    SEK("Swedish Krona", "kr"),
    CNY("Yuan Renminbi", "¥"),
    JPY("Yen", "¥"),
    ISK("Iceland Krona", "kr"),
    ILS("New Israeli Sheqel", "₪"),
    TRY("Turkish Lira", "₺");

    private final String currencyName;
    private final String currencySymbol;

    CurrencyCode(String currencyName, String currencySymbol) {
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
