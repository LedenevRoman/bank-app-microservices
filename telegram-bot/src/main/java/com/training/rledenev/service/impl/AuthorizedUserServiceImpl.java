package com.training.rledenev.service.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.entity.Chat;
import com.training.rledenev.enums.Role;
import com.training.rledenev.repository.ChatRepository;
import com.training.rledenev.service.AuthorizedUserService;
import com.training.rledenev.service.action.NameActionServiceMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static com.training.rledenev.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class AuthorizedUserServiceImpl implements AuthorizedUserService {
    private final BankAppServiceClient bankAppServiceClient;
    private final ChatRepository chatRepository;

    @Override
    public SendMessage handleRequests(Chat chat, String message) {
        long chatId = chat.getId();
        try {
            Role userRole = bankAppServiceClient.getRole();
            if (message.equals(BACK)) {
                chat.setActionName(null);
                chat.setAgreementDto(null);
                chat.setChosenAgreementId(null);
                chat.setAccountDto(null);
                chat.setTransactionDto(null);
                chatRepository.save(chat);
                return createSendMessageWithButtons(chatId, SELECT_ACTION, getListOfActionsByUserRole(userRole));
            }
            String actionName = chat.getActionName();
            if (actionName != null) {
                return NameActionServiceMap.get(actionName).handleMessage(chat, message, userRole);
            } else if (NameActionServiceMap.containsKey(message)) {
                chat.setActionName(message);
                chatRepository.save(chat);
                return NameActionServiceMap.get(message).handleMessage(chat, message, userRole);
            }
            return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, getListOfActionsByUserRole(userRole));
        } catch (ResponseStatusException exception) {
            chatRepository.delete(chat);
            return createSendMessageWithButtons(chatId, SESSION_CLOSED, List.of(REGISTER_USER, LOG_IN));
        }
    }
}
