package com.training.rledenev.service;

import com.training.rledenev.entity.Chat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AuthorizedUserService {
    SendMessage handleRequests(Chat chat, String message);
}
