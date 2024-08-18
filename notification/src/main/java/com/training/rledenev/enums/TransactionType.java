package com.training.rledenev.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    TRANSFER("Transfer"),
    PAYMENT("Payment"),
    CASH("Cash"),
    DEPOSIT("Deposit");

    private final String simpleName;

    TransactionType(String simpleName) {
        this.simpleName = simpleName;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
