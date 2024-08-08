package com.training.rledenev.service.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.client.FeignClientJwtTokenHolder;
import com.training.rledenev.dto.UserDto;
import com.training.rledenev.service.LogInUserService;
import com.training.rledenev.service.chatmaps.ChatIdInLoginMap;
import com.training.rledenev.service.chatmaps.ChatIdSecurityTokenMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.training.rledenev.service.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class LogInUserServiceImpl implements LogInUserService {
    private static final Map<Long, String> CHAT_ID_EMAIL_MAP = new ConcurrentHashMap<>();
    private final BankAppServiceClient bankAppServiceClient;

    @Override
    public SendMessage handleLogInRequests(long chatId, String messageText) {
        if (CHAT_ID_EMAIL_MAP.containsKey(chatId)) {
            UserDto requestUserDto = new UserDto();
            requestUserDto.setEmail(CHAT_ID_EMAIL_MAP.get(chatId));
            requestUserDto.setPassword(messageText);
            try {
                String token = bankAppServiceClient.auth(requestUserDto);
                ChatIdSecurityTokenMap.put(chatId, token);
                FeignClientJwtTokenHolder.setToken(token);
                UserDto currentUser = bankAppServiceClient.getCurrentUser();
                return createSendMessageWithButtons(chatId,
                        String.format(AUTHENTICATION_COMPLETED, currentUser.getFirstName(), currentUser.getLastName()),
                        getListOfActionsByUserRole(currentUser.getRole()));
            } catch (ResponseStatusException exception) {
                return createSendMessageWithButtons(chatId, AUTHENTICATION_FAILED, List.of(REGISTER_USER, LOG_IN));
            } finally {
                CHAT_ID_EMAIL_MAP.remove(chatId);
                ChatIdInLoginMap.put(chatId, false);
                FeignClientJwtTokenHolder.clear();
            }
        } else {
            CHAT_ID_EMAIL_MAP.put(chatId, messageText);
            return createSendMessage(chatId, ENTER_PASSWORD);
        }
    }
}
