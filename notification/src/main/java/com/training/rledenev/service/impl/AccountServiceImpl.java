package com.training.rledenev.service.impl;

import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.mapper.AccountMapper;
import com.training.rledenev.repository.AccountRepository;
import com.training.rledenev.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountDto(Long agreementId) {
        return accountMapper.mapToDto(accountRepository.findByAgreement_Id(agreementId));
    }
}
