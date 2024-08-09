package com.training.rledenev.service.action.impl.transaction.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.dto.TransactionDto;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.TransactionType;
import com.training.rledenev.service.action.impl.transaction.TransactionMessageHandlerService;
import com.training.rledenev.service.chatmaps.ChatIdTransactionDtoMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.List;

import static com.training.rledenev.service.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class TransactionMessageHandlerServiceImpl implements TransactionMessageHandlerService {
    private final BankAppServiceClient bankAppServiceClient;

    @Override
    public SendMessage handleMessage(long chatId, String message, AccountDto accountDto) {
        if (ChatIdTransactionDtoMap.get(chatId) == null) {
            return handleInitialTransactionMessage(chatId, accountDto);
        } else {
            return handleCreationTransactionMessage(chatId, message);
        }
    }

    private static SendMessage handleInitialTransactionMessage(long chatId, AccountDto accountDto) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setDebitAccountNumber(accountDto.getNumber());
        ChatIdTransactionDtoMap.put(chatId, transactionDto);
        return createSendMessage(chatId, ENTER_ACCOUNT_NUMBER);
    }

    private SendMessage handleCreationTransactionMessage(long chatId, String message) {
        TransactionDto transactionDto = ChatIdTransactionDtoMap.get(chatId);
        SendMessage fillInMessage = fillInTransactionDtoMessage(chatId, message, transactionDto);
        if (fillInMessage != null) {
            return fillInMessage;
        }
        if (message.equals(CONFIRM)) {
            return createNewTransactionMessage(chatId, transactionDto);
        }
        if (message.equals(CANCEL)) {
            ChatIdTransactionDtoMap.remove(chatId);
            return createSendMessageWithButtons(chatId, TRANSACTION_CANCELED, List.of(BACK_TO_LIST_ACCOUNTS));
        } else {
            return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, List.of(EXIT));
        }
    }

    private SendMessage fillInTransactionDtoMessage(long chatId, String message, TransactionDto transactionDto) {
        if (transactionDto.getCreditAccountNumber() == null) {
            transactionDto.setCreditAccountNumber(message);
            return createSendMessageWithButtons(chatId, SELECT_CURRENCY, getCurrencyButtons());
        }
        if (transactionDto.getCurrencyCode() == null) {
            transactionDto.setCurrencyCode(CurrencyCode.valueOf(message.toUpperCase()));
            return createSendMessage(chatId, ENTER_AMOUNT);
        }
        if (transactionDto.getAmount() == null) {
            try {
                transactionDto.setAmount(BigDecimal.valueOf(Double.parseDouble(message)));
                if (Double.parseDouble(message) <= 0) {
                    throw new NumberFormatException();
                }
                return createSendMessageWithButtons(chatId, SELECT_TYPE, getTypeButtons());
            } catch (NumberFormatException e) {
                return createSendMessage(chatId, INCORRECT_NUMBER);
            }
        }
        if (transactionDto.getType() == null && getTypeButtons().contains(message)) {
            transactionDto.setType(TransactionType.valueOf(message.toUpperCase()));
            return createSendMessage(chatId, ENTER_DESCRIPTION);
        }
        if (transactionDto.getDescription() == null) {
            transactionDto.setDescription(message);
            return createSendMessageWithButtons(chatId, getTransactionSummaryMessage(transactionDto),
                    List.of(CONFIRM, CANCEL));
        }
        return null;
    }

    private SendMessage createNewTransactionMessage(long chatId, TransactionDto transactionDto) {
        ChatIdTransactionDtoMap.remove(chatId);
        try {
            bankAppServiceClient.createTransaction(transactionDto);
            return createSendMessageWithButtons(chatId, TRANSACTION_COMPLETED, List.of(BACK_TO_LIST_ACCOUNTS));
        } catch (ResponseStatusException exception) {
            return createSendMessageWithButtons(chatId, TRANSACTION_FAILED + exception.getReason(),
                    List.of(BACK_TO_LIST_ACCOUNTS));
        }
    }

    private String getTransactionSummaryMessage(TransactionDto transactionDto) {
        return String.format(TRANSACTION_INFO, transactionDto.getCreditAccountNumber(), transactionDto.getAmount(),
                transactionDto.getCurrencyCode(), transactionDto.getType(), transactionDto.getDescription());
    }
}
