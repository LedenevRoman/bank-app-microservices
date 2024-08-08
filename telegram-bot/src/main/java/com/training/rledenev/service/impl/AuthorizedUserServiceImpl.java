package com.training.rledenev.service.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.enums.Role;
import com.training.rledenev.service.AuthorizedUserService;
import com.training.rledenev.service.chatmaps.ChatIdActionNameMap;
import com.training.rledenev.service.chatmaps.ChatIdSecurityTokenMap;
import com.training.rledenev.service.chatmaps.NameActionServiceMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static com.training.rledenev.service.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class AuthorizedUserServiceImpl implements AuthorizedUserService {
    private final BankAppServiceClient bankAppServiceClient;

    @Override
    public SendMessage handleRequests(String message, long chatId) {
        try {
            Role userRole = bankAppServiceClient.getRole();
            if (message.equals(BACK)) {
                removeIdFromMaps(chatId);
                return createSendMessageWithButtons(chatId, SELECT_ACTION, getListOfActionsByUserRole(userRole));
            }
            String actionName = ChatIdActionNameMap.get(chatId);
            if (actionName != null) {
                return NameActionServiceMap.get(actionName).handleMessage(chatId, message, userRole);
            } else if (NameActionServiceMap.containsKey(message)) {
                ChatIdActionNameMap.put(chatId, message);
                return NameActionServiceMap.get(message).handleMessage(chatId, message, userRole);
            }
            return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, getListOfActionsByUserRole(userRole));
        } catch (ResponseStatusException exception) {
            ChatIdSecurityTokenMap.remove(chatId);
            removeIdFromMaps(chatId);
            return createSendMessageWithButtons(chatId, SESSION_CLOSED, List.of(REGISTER_USER, LOG_IN));
        }
    }
}
