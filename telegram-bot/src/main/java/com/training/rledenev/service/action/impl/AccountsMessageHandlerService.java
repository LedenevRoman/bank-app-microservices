package com.training.rledenev.service.action.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.dto.TransactionDto;
import com.training.rledenev.entity.Chat;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.Role;
import com.training.rledenev.repository.ChatRepository;
import com.training.rledenev.service.action.ActionMessageHandlerService;
import com.training.rledenev.service.action.impl.transaction.TransactionMessageHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.training.rledenev.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class AccountsMessageHandlerService implements ActionMessageHandlerService {
    private final BankAppServiceClient bankAppServiceClient;
    private final TransactionMessageHandlerService transactionMessageHandlerService;
    private final ChatRepository chatRepository;

    @Override
    public SendMessage handleMessage(Chat chat, String message, Role userRole) {
        List<AccountDto> accountDtos = bankAppServiceClient.getAccountsForClient();
        if (chat.getAccountDto() == null) {
            return handleInitialAccountActionMessage(chat, message, accountDtos);
        } else {
            if (message.equals(BACK_TO_LIST_ACCOUNTS)) {
                chat.setAccountDto(null);
                chat.setMakingTransaction(false);
                chat.setTransactionDto(null);
                chatRepository.save(chat);
                return createSendMessageWithButtons(chat.getId(), getMyAccountsMessage(accountDtos),
                        getMyAccountsButtons(accountDtos));
            }
            return handleAccountActionOptionsMessage(chat, message);
        }
    }

    private SendMessage handleAccountActionOptionsMessage(Chat chat, String message) {
        AccountDto accountDto = chat.getAccountDto();
        if (chat.isMakingTransaction()) {
            return transactionMessageHandlerService.handleMessage(chat, message, accountDto);
        } else if (message.equals(MAKE_TRANSACTION)) {
            chat.setMakingTransaction(true);
            chatRepository.save(chat);
            return transactionMessageHandlerService.handleMessage(chat, message, accountDto);
        }
        if (message.equals(VIEW_ALL_TRANSACTIONS)) {
            List<TransactionDto> allTransactionsDto = bankAppServiceClient
                    .getAllTransactionsOfAccount(accountDto.getNumber());
            return createSendMessageWithButtons(chat.getId(), getAllTransactionsMessage(accountDto, allTransactionsDto),
                    List.of(BACK_TO_LIST_ACCOUNTS));
        } else {
            return createSendMessageWithButtons(chat.getId(), UNKNOWN_INPUT_MESSAGE, List.of(EXIT));
        }
    }

    private SendMessage handleInitialAccountActionMessage(Chat chat, String message, List<AccountDto> accountDtos) {
        if (message.equals(MY_ACCOUNTS)) {
            return createSendMessageWithButtons(chat.getId(), getMyAccountsMessage(accountDtos),
                    getMyAccountsButtons(accountDtos));
        }
        return handleAccountSelectionMessage(chat, message, accountDtos);
    }

    private SendMessage handleAccountSelectionMessage(Chat chat, String message, List<AccountDto> accountDtos) {
        long chatId = chat.getId();
        int accountIndex;
        try {
            accountIndex = Integer.parseInt(message);
        } catch (NumberFormatException exception) {
            return createSendMessageWithButtons(chatId, INCORRECT_NUMBER_INT,
                    getMyAccountsButtons(accountDtos));
        }
        if (accountIndex > 0 && accountIndex <= accountDtos.size()) {
            AccountDto accountDto = accountDtos.get(accountIndex - 1);
            chat.setAccountDto(accountDto);
            chatRepository.save(chat);
            return createSendMessageWithButtons(chatId, getCustomAccountInfo(accountDto),
                    getListOfActionsForClientAccount());
        } else {
            return createSendMessageWithButtons(chatId, WRONG_ACCOUNT_INDEX,
                    getMyAccountsButtons(accountDtos));
        }
    }

    private String getAllTransactionsMessage(AccountDto accountDto, List<TransactionDto> allTransactionsDto) {
        StringBuilder stringBuilder = new StringBuilder(LIST_TRANSACTIONS);
        allTransactionsDto.forEach(transactionDto -> {
            if (transactionDto.getDebitAccountNumber().equals(accountDto.getNumber())) {
                getDebitAccountMessage(stringBuilder, transactionDto, accountDto.getCurrencyCode());
            } else {
                getCreditAccountMessage(stringBuilder, transactionDto, accountDto.getCurrencyCode());
            }
        });

        return stringBuilder.toString();
    }

    private void getDebitAccountMessage(StringBuilder stringBuilder, TransactionDto transactionDto,
                                        CurrencyCode accountCurrency) {
        if (transactionDto.getCurrencyCode() == accountCurrency) {
            stringBuilder.append(String.format(AMOUNT_IN_SAME_CURRENCY_DEBIT_TRANSACTION_INFO,
                    transactionDto.getAmount(), transactionDto.getCurrencyCode(),
                    transactionDto.getCreditAccountNumber()));
        } else {
            stringBuilder.append(String.format(AMOUNT_DEBIT_TRANSACTION_INFO, transactionDto.getAmount(),
                    transactionDto.getCurrencyCode(), transactionDto.getDebitBalanceDifference(), accountCurrency,
                    transactionDto.getCreditAccountNumber()));
        }
        appendAnotherInfo(stringBuilder, transactionDto);
    }

    private void getCreditAccountMessage(StringBuilder stringBuilder, TransactionDto transactionDto,
                                         CurrencyCode accountCurrency) {
        if (transactionDto.getCurrencyCode() == accountCurrency) {
            stringBuilder.append(String.format(AMOUNT_IN_SAME_CURRENCY_CREDIT_TRANSACTION_INFO,
                    transactionDto.getAmount(), transactionDto.getCurrencyCode(),
                    transactionDto.getDebitAccountNumber()));
        } else {
            stringBuilder.append(String.format(AMOUNT_CREDIT_TRANSACTION_INFO, transactionDto.getAmount(),
                    transactionDto.getCurrencyCode(), transactionDto.getCreditBalanceDifference(), accountCurrency,
                    transactionDto.getDebitAccountNumber()));
        }
        appendAnotherInfo(stringBuilder, transactionDto);
    }

    private static void appendAnotherInfo(StringBuilder stringBuilder, TransactionDto transactionDto) {
        stringBuilder.append(String.format(Locale.ENGLISH, ANOTHER_TRANSACTION_INFO, transactionDto.getCreatedAt(),
                transactionDto.getType(), transactionDto.getDescription()));
    }

    private String getCustomAccountInfo(AccountDto accountDto) {
        LocalDate startDate = accountDto.getStartDate();
        LocalDate paymentTerm = accountDto.getPaymentTerm();
        return String.format(FULL_ACCOUNT_INFO, accountDto.getNumber(), accountDto.getOwnerFullName(),
                accountDto.getProductName(), accountDto.getInterestRate(), startDate, startDate, startDate, paymentTerm,
                paymentTerm, paymentTerm, accountDto.getBalance(), accountDto.getCurrencyName());
    }

    private List<String> getMyAccountsButtons(List<AccountDto> accountDtos) {
        List<String> accountsIndex = new ArrayList<>();
        for (int i = 0; i < accountDtos.size(); i++) {
            accountsIndex.add(String.valueOf(i + 1));
        }
        accountsIndex.add(BACK);
        return accountsIndex;
    }

    private String getMyAccountsMessage(List<AccountDto> accountDtos) {
        StringBuilder stringBuilder = new StringBuilder(MY_ACCOUNTS_LIST);
        for (int i = 0; i < accountDtos.size(); i++) {
            AccountDto accountDto = accountDtos.get(i);
            stringBuilder.append(String.format(SHORT_ACCOUNT_INFO, i + 1, accountDto.getNumber(),
                            accountDto.getProductName(), accountDto.getBalance(), accountDto.getCurrencyCode()))
                    .append("\n")
                    .append("\n");
        }
        stringBuilder.append("\n")
                .append(SELECT_ACCOUNT);
        return stringBuilder.toString();
    }
}
