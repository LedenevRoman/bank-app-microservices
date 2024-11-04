package com.training.rledenev.service.impl;

import com.training.rledenev.dto.TransactionDto;
import com.training.rledenev.entity.User;
import com.training.rledenev.repository.UserRepository;
import com.training.rledenev.service.MailSender;
import com.training.rledenev.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final MailSender mailSender;
    private final UserRepository userRepository;

    @Override
    public void handleTransaction(TransactionDto transactionDto) {
        User debitAccountOwner = userRepository.findDebitAccountOwner(transactionDto.getId());
        User creditAccountOwner = userRepository.findCreditAccountOwner(transactionDto.getId());
        sendDebitMail(debitAccountOwner, transactionDto);
        sendCreditMail(creditAccountOwner, transactionDto);
    }

    private void sendDebitMail(User debitAccountOwner, TransactionDto transactionDto) {
        String[] to = new String[]{debitAccountOwner.getEmail()};
        String subject = "Transaction notification";
        String template = "debit-account-transaction";
        Map<String, Object> templateVars = getTemplateVars(debitAccountOwner, transactionDto);
        templateVars.put("debitCurrencyCode", transactionDto.getDebitCurrencyCode().getCurrencySymbol());
        templateVars.put("debitBalanceDifference", getFormattedAmount(transactionDto.getDebitBalanceDifference()));

        mailSender.send(to, subject, template, templateVars);
    }

    private void sendCreditMail(User creditAccountOwner, TransactionDto transactionDto) {
        String[] to = new String[]{creditAccountOwner.getEmail()};
        String subject = "Transaction notification";
        String template = "credit-account-transaction";
        Map<String, Object> templateVars = getTemplateVars(creditAccountOwner, transactionDto);
        templateVars.put("creditCurrencyCode", transactionDto.getDebitCurrencyCode().getCurrencySymbol());
        templateVars.put("creditBalanceDifference", getFormattedAmount(transactionDto.getDebitBalanceDifference()));

        mailSender.send(to, subject, template, templateVars);
    }

    private Map<String, Object> getTemplateVars(User accountOwner, TransactionDto transactionDto) {
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("clientFullName", getUserFullName(accountOwner));
        templateVars.put("debitAccountNumber", transactionDto.getDebitAccountNumber());
        templateVars.put("creditAccountNumber", transactionDto.getCreditAccountNumber());
        templateVars.put("amount", getFormattedAmount(transactionDto.getAmount()));
        templateVars.put("currencyCode", transactionDto.getCurrencyCode().getCurrencySymbol());
        templateVars.put("type", transactionDto.getType());
        templateVars.put("description", transactionDto.getDescription());
        return templateVars;
    }

    private String getFormattedAmount(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(amount);
    }

    private String getUserFullName(User user) {
        return String.format("%s %s", user.getFirstName(), user.getLastName());
    }
}
