package com.training.rledenev.controller;

import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/account")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/all/client")
    public List<AccountDto> getAccountsForClient() {
        return accountService.getAccountsForClient();
    }
}
