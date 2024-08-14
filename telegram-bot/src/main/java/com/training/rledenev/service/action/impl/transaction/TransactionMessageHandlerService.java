package com.training.rledenev.service.action.impl.transaction;

import com.training.rledenev.dto.AccountDto;
import com.training.rledenev.entity.Chat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface TransactionMessageHandlerService {
    SendMessage handleMessage(Chat chat, String message, AccountDto accountDto);
}
