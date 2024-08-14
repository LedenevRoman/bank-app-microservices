package com.training.rledenev.service.impl;

import com.training.rledenev.client.FeignClientJwtTokenHolder;
import com.training.rledenev.entity.Chat;
import com.training.rledenev.repository.ChatRepository;
import com.training.rledenev.service.AuthorizedUserService;
import com.training.rledenev.service.LogInUserService;
import com.training.rledenev.service.RegistrationUserService;
import com.training.rledenev.service.UpdateHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

import static com.training.rledenev.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class UpdateHandlerServiceImpl implements UpdateHandlerService {
    private final RegistrationUserService registrationUserService;
    private final LogInUserService logInUserService;
    private final AuthorizedUserService authorizedUserService;
    private final ChatRepository chatRepository;

    @Override
    public SendMessage handleUpdate(Update update) {
        long chatId = update.getMessage().getChatId();
        if (checkMessageExists(update)) {
            return handleMessage(chatId, update);
        }
        return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, List.of(EXIT));
    }

    private SendMessage handleMessage(long chatId, Update update) {
        String messageText = update.getMessage().getText();
        if (messageText.equals(EXIT)) {
            chatRepository.deleteById(chatId);
            return createSendMessageWithButtons(chatId, WELCOME_MESSAGE, List.of(REGISTER_USER, LOG_IN));
        }
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            if (chat.isInRegistration()) {
                return registrationUserService.handleRegistrationRequests(chat, messageText, update);
            }
            if (chat.isInLogin()) {
                return logInUserService.handleLogInRequests(chat, messageText);
            }
            if (chat.getSecurityToken() != null) {
                return handleAuthorizedMessage(chat, messageText);
            }
        } else {
            return handleUnauthorizedMessage(chatId, messageText);
        }
        return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, List.of(EXIT));
    }

    private SendMessage handleAuthorizedMessage(Chat chat, String messageText) {
        try {
            FeignClientJwtTokenHolder.setToken(chat.getSecurityToken());
            return authorizedUserService.handleRequests(chat, messageText);
        } finally {
            FeignClientJwtTokenHolder.clear();
        }
    }

    private SendMessage handleUnauthorizedMessage(long chatId, String messageText) {
        if (messageText.equals(START)) {
            return createSendMessageWithButtons(chatId, WELCOME_MESSAGE, List.of(REGISTER_USER, LOG_IN));
        }
        if (messageText.equals(REGISTER_USER)) {
            Chat chat = new Chat();
            chat.setId(chatId);
            chat.setInRegistration(true);
            chatRepository.save(chat);
            return createSendMessage(chatId, ENTER_FIRST_NAME);
        }
        if (messageText.equals(LOG_IN)) {
            Chat chat = new Chat();
            chat.setId(chatId);
            chat.setInLogin(true);
            chatRepository.save(chat);
            return createSendMessage(chatId, ENTER_EMAIL);
        }
        return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, List.of(EXIT));
    }

    private static boolean checkMessageExists(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }
}
