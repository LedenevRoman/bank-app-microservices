package com.training.rledenev.service;

import com.training.rledenev.entity.Chat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface RegistrationUserService {
    SendMessage handleRegistrationRequests(Chat chat, String messageText, Update update);
}
