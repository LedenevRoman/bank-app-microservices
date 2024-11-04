package com.training.rledenev.service.impl;

import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.entity.Account;
import com.training.rledenev.entity.User;
import com.training.rledenev.exception.AccountNotFoundException;
import com.training.rledenev.mapper.AccountMapper;
import com.training.rledenev.repository.AccountRepository;
import com.training.rledenev.security.UserProvider;
import com.training.rledenev.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final UserProvider userProvider;

    @Transactional(readOnly = true)
    @Override
    public boolean isAccountNumberExists(String number) {
        return accountRepository.isAccountNumberExists(number);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDto> getAccountsForClient() {
        User client = userProvider.getCurrentUser();
        return accountMapper.mapToListDtos(accountRepository.getAccountsOfClient(client));
    }

    @Transactional(readOnly = true)
    @Override
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number " + accountNumber));
    }
}
