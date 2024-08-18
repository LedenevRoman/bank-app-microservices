package com.training.rledenev.service.impl;

import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.repository.UserRepository;
import com.training.rledenev.service.AccountService;
import com.training.rledenev.service.AgreementService;
import com.training.rledenev.service.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgreementServiceImpl implements AgreementService {
    private final MailSender mailSender;
    private final UserRepository userRepository;
    private final AccountService accountService;


    @Override
    public void handleAgreement(AgreementDto agreementDto) {
        switch (agreementDto.getStatus()) {
            case NEW -> handleNewAgreement(agreementDto);
            case ACTIVE -> handleConfirmedAgreement(agreementDto);
            case BLOCKED -> handleBlockedAgreement(agreementDto);
        }
    }

    private void handleNewAgreement(AgreementDto agreementDto) {
        String[] to = userRepository.findManagersEmails();
        String subject = "New Agreement Approval";
        String template = "new-agreement";
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("clientName", userRepository.findFullNameByEmail(agreementDto.getClientEmail()));
        templateVars.put("productName", agreementDto.getProductName());
        templateVars.put("amount", agreementDto.getSum());
        templateVars.put("period", getStringFormattedPeriod(agreementDto.getPeriodMonths()));

        mailSender.send(to, subject, template, templateVars);
    }

    private void handleConfirmedAgreement(AgreementDto agreementDto) {
        String[] to = new String[]{agreementDto.getClientEmail()};
        String subject = "Agreement confirmed";
        String template = "agreement-confirmed";
        AccountDto accountDto = accountService.getAccountDto(agreementDto.getId());
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("accountNumber", accountDto.getNumber());
        templateVars.put("ownerFullName", accountDto.getOwnerFullName());
        templateVars.put("productName", accountDto.getProductName());
        templateVars.put("interestRate", accountDto.getInterestRate().setScale(2, RoundingMode.UNNECESSARY));
        templateVars.put("startDate", accountDto.getStartDate());
        templateVars.put("paymentTerm", accountDto.getPaymentTerm());
        templateVars.put("balance", accountDto.getBalance());
        templateVars.put("currencyName", accountDto.getCurrencyName());

        mailSender.send(to, subject, template, templateVars);
    }

    private void handleBlockedAgreement(AgreementDto agreementDto) {
        String[] to = new String[]{agreementDto.getClientEmail()};
        String subject = "Agreement blocked";
        String template = "agreement-blocked";
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("clientName", userRepository.findFullNameByEmail(agreementDto.getClientEmail()));
        templateVars.put("managerName", userRepository.findManagerFullNameByAgreementId(agreementDto.getId()));

        mailSender.send(to, subject, template, templateVars);
    }

    public static String getStringFormattedPeriod(Integer periodMonths) {
        int years = periodMonths / 12;
        int remainingMonths = periodMonths % 12;

        String result = "";
        if (years > 0) {
            result += years + " " + (years == 1 ? "year" : "years");
        }
        if (remainingMonths > 0) {
            if (!result.isEmpty()) {
                result += " ";
            }
            result += remainingMonths + " " + (remainingMonths == 1 ? "month" : "months");
        }

        return result;
    }
}
