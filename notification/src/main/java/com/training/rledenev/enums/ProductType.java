package com.training.rledenev.enums;

import lombok.Getter;

@Getter
public enum ProductType {
    LOAN("Loan"),
    DEPOSIT("Deposit"),
    DEBIT_CARD("Debit card"),
    CREDIT_CARD("Credit card");

    private final String simpleName;

    ProductType(String simpleName) {
        this.simpleName = simpleName;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
