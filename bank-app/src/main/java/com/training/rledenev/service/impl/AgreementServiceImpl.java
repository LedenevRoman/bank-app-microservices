package com.training.rledenev.service.impl;

import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.entity.Account;
import com.training.rledenev.entity.Agreement;
import com.training.rledenev.entity.Product;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.enums.Status;
import com.training.rledenev.exception.AgreementNotFoundException;
import com.training.rledenev.exception.ProductNotFoundException;
import com.training.rledenev.kafka.KafkaProducer;
import com.training.rledenev.mapper.AgreementMapper;
import com.training.rledenev.repository.AccountRepository;
import com.training.rledenev.repository.AgreementRepository;
import com.training.rledenev.repository.ProductRepository;
import com.training.rledenev.security.UserProvider;
import com.training.rledenev.service.AccountService;
import com.training.rledenev.service.AgreementService;
import com.training.rledenev.service.TransactionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgreementServiceImpl implements AgreementService {
    private final AgreementRepository agreementRepository;
    private final AgreementMapper agreementMapper;
    private final UserProvider userProvider;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final KafkaProducer kafkaProducer;
    private final AgreementService agreementService;

    public AgreementServiceImpl(AgreementRepository agreementRepository, AgreementMapper agreementMapper,
                                UserProvider userProvider, ProductRepository productRepository,
                                AccountRepository accountRepository, AccountService accountService,
                                TransactionService transactionService, KafkaProducer kafkaProducer,
                                @Lazy AgreementService agreementService) {
        this.agreementRepository = agreementRepository;
        this.agreementMapper = agreementMapper;
        this.userProvider = userProvider;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.kafkaProducer = kafkaProducer;
        this.agreementService = agreementService;
    }

    @Override
    public AgreementDto createAgreementWithNotification(AgreementDto agreementDto) {
        AgreementDto createdAgreementDto = agreementService.createAgreement(agreementDto);
        kafkaProducer.sendAgreement(createdAgreementDto);
        return createdAgreementDto;
    }

    @Transactional
    @Override
    public List<AgreementDto> getAgreementsForManager() {
        return agreementMapper.mapToListDtos(agreementRepository.findAllNewAgreements());
    }

    @Override
    public void confirmAgreementWithNotification(Long agreementId) {
        AgreementDto agreementDto = agreementService.confirmAgreement(agreementId);
        kafkaProducer.sendAgreement(agreementDto);
    }

    @Transactional
    @Override
    public AgreementDto confirmAgreement(Long agreementId) {
        Agreement agreement = getAndUpdateAgreement(agreementId);
        Account account = createAccount(agreement);
        agreement.setAccount(account);
        agreement.setStatus(Status.ACTIVE);
        agreement.setStartDate(LocalDate.now());
        return agreementMapper.mapToDto(agreement);
    }

    @Override
    public void blockAgreementWithNotification(Long agreementId) {
        AgreementDto agreementDto = agreementService.blockAgreement(agreementId);
        kafkaProducer.sendAgreement(agreementDto);
    }

    @Transactional
    @Override
    public AgreementDto blockAgreement(Long agreementId) {
        Agreement agreement = getAndUpdateAgreement(agreementId);
        agreement.setStatus(Status.BLOCKED);
        return agreementMapper.mapToDto(agreement);
    }

    @Transactional
    @Override
    public AgreementDto getAgreementDtoById(Long id) {
        return agreementMapper.mapToDto(findById(id));
    }

    @Transactional
    @Override
    public AgreementDto createAgreement(AgreementDto agreementDto) {
        Agreement agreement = agreementMapper.mapToEntity(agreementDto);
        agreement.setClient(userProvider.getCurrentUser());
        agreement.setStatus(Status.NEW);
        Product product = productRepository.findActiveProductByName(agreementDto.getProductName())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        agreement.setProduct(product);
        agreement.setCreatedAt(LocalDateTime.now());
        agreement.setUpdatedAt(LocalDateTime.now());
        agreementRepository.save(agreement);
        return agreementMapper.mapToDto(agreement);
    }

    private Agreement getAndUpdateAgreement(Long agreementId) {
        Agreement agreement = findById(agreementId);
        agreement.setManager(userProvider.getCurrentUser());
        agreement.setUpdatedAt(LocalDateTime.now());
        return agreement;
    }

    private Agreement findById(Long id) {
        return agreementRepository.findById(id)
                .orElseThrow(() -> new AgreementNotFoundException("Agreement not found with id = " + id));
    }

    private Account createAccount(Agreement agreement) {
        Account account = new Account();
        account.setClient(userProvider.getCurrentUser());
        String number = RandomStringUtils.randomNumeric(16);
        while (accountService.isAccountNumberExists(number)) {
            number = RandomStringUtils.randomNumeric(16);
        }
        account.setNumber(number);
        account.setStatus(Status.NEW);
        account.setAgreement(agreement);
        account.setCurrencyCode(agreement.getCurrencyCode());

        if (agreement.getProduct().getType() == ProductType.LOAN
                || agreement.getProduct().getType() == ProductType.CREDIT_CARD) {
            transactionService.giveCreditFundsToAccount(account, agreement.getSum());
        } else {
            account.setBalance(agreement.getSum());
        }
        account.setUpdatedAt(LocalDateTime.now());
        account.setStatus(Status.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }
}
