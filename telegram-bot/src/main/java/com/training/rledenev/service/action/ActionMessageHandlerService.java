package com.training.rledenev.service.action;

import com.training.rledenev.entity.Chat;
import com.training.rledenev.enums.Role;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ActionMessageHandlerService {
    SendMessage handleMessage(Chat chat, String message, Role userRole);
}
