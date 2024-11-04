package com.training.rledenev.controller;

import com.training.rledenev.dto.TransactionDto;
import com.training.rledenev.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/all")
    public List<TransactionDto> getAllTransactionsOfAccount(@RequestParam String accountNumber) {
        return transactionService.getAllTransactionsOfAccount(accountNumber);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransaction(@RequestBody TransactionDto transactionDto) {
        transactionService.createTransactionWithNotification(transactionDto);
    }
}
