package com.training.rledenev.service;

import com.training.rledenev.dto.TransactionDto;

public interface TransactionService {
    void handleTransaction(TransactionDto transactionDto);
}
