package com.training.rledenev.service.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.client.FeignClientJwtTokenHolder;
import com.training.rledenev.dto.UserDto;
import com.training.rledenev.entity.Chat;
import com.training.rledenev.repository.ChatRepository;
import com.training.rledenev.service.LogInUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static com.training.rledenev.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class LogInUserServiceImpl implements LogInUserService {
    private final BankAppServiceClient bankAppServiceClient;
    private final ChatRepository chatRepository;

    @Override
    public SendMessage handleLogInRequests(Chat chat, String messageText) {
        long chatId = chat.getId();
        String email = chat.getEmail();
        if (email != null) {
            UserDto requestUserDto = new UserDto();
            requestUserDto.setEmail(email);
            requestUserDto.setPassword(messageText);
            try {
                String token = bankAppServiceClient.auth(requestUserDto);
                chat.setSecurityToken(token);
                FeignClientJwtTokenHolder.setToken(token);
                UserDto currentUser = bankAppServiceClient.getCurrentUser();
                return createSendMessageWithButtons(chatId,
                        String.format(AUTHENTICATION_COMPLETED, currentUser.getFirstName(), currentUser.getLastName()),
                        getListOfActionsByUserRole(currentUser.getRole()));
            } catch (ResponseStatusException exception) {
                chatRepository.delete(chat);
                return createSendMessageWithButtons(chatId, AUTHENTICATION_FAILED, List.of(REGISTER_USER, LOG_IN));
            } finally {
                chat.setInLogin(false);
                chatRepository.save(chat);
                FeignClientJwtTokenHolder.clear();
            }
        } else {
            chat.setEmail(messageText);
            chatRepository.save(chat);
            return createSendMessage(chatId, ENTER_PASSWORD);
        }
    }
}
