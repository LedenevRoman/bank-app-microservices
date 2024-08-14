package com.training.rledenev.service.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.dto.UserDto;
import com.training.rledenev.entity.Chat;
import com.training.rledenev.repository.ChatRepository;
import com.training.rledenev.service.RegistrationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.training.rledenev.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class RegistrationUserServiceImpl implements RegistrationUserService {
    private static final String NAME_PATTERN = "[A-Za-z]+";
    private static final String PHONE_PATTERN = "^\\+\\d{1,3}-?\\d{3,14}$";
    private static final String ADDRESS_PATTERN = "[A-Za-z0-9\\s.,\\-'/\\\\]+";
    private static final String EMAIL_PATTERN =
            "(?!.*\\.{2})[A-Za-z0-9][A-Za-z0-9.]{4,28}[A-Za-z0-9]@[A-Za-z0-9.]+\\.[A-Za-z]{2,}";
    private static final String PASSWORD_PATTERN =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\p{Punct})[a-zA-Z\\d\\p{Punct}]*$";
    private final BankAppServiceClient bankAppServiceClient;
    private final ChatRepository chatRepository;

    @Override
    public SendMessage handleRegistrationRequests(Chat chat, String messageText, Update update) {
        if (chat.getUserDto() == null) {
            return fillInFirstName(chat, messageText);
        }
        UserDto userDto = chat.getUserDto();
        if (userDto.getLastName() == null) {
            return fillInLastName(chat, messageText);
        }
        if (userDto.getPhone() == null) {
            return fillInPhone(chat, messageText);
        }
        if (userDto.getAddress() == null) {
            return fillInAddress(chat, messageText);
        }
        if (userDto.getEmail() == null) {
            return fillInEmail(chat, messageText);
        }
        return fillInPassword(chat, messageText);
    }

    private SendMessage fillInFirstName(Chat chat, String messageText) {
        UserDto userDto = new UserDto();
        if (isValidName(messageText)) {
            userDto.setFirstName(messageText);
            chat.setUserDto(userDto);
            chatRepository.save(chat);
            return createSendMessage(chat.getId(), ENTER_LAST_NAME);
        } else {
            return createSendMessage(chat.getId(), INCORRECT_NAME);
        }
    }

    private SendMessage fillInLastName(Chat chat, String messageText) {
        if (isValidName(messageText)) {
            chat.getUserDto().setLastName(messageText);
            chatRepository.save(chat);
            return createSendMessage(chat.getId(), ENTER_PHONE);
        } else {
            return createSendMessage(chat.getId(), INCORRECT_NAME);
        }
    }

    private SendMessage fillInPhone(Chat chat, String messageText) {
        if (isValidPhone(messageText)) {
            chat.getUserDto().setPhone(messageText);
            chatRepository.save(chat);
            return createSendMessage(chat.getId(), ENTER_ADDRESS);
        } else {
            return createSendMessage(chat.getId(), INCORRECT_PHONE);
        }
    }

    private SendMessage fillInAddress(Chat chat, String messageText) {
        if (isValidAddress(messageText)) {
            chat.getUserDto().setAddress(messageText);
            chatRepository.save(chat);
            return createSendMessage(chat.getId(), ENTER_EMAIL);
        } else {
            return createSendMessage(chat.getId(), INCORRECT_ADDRESS);
        }
    }

    private SendMessage fillInEmail(Chat chat, String messageText) {
        if (isValidEmail(messageText)) {
            chat.getUserDto().setEmail(messageText);
            chatRepository.save(chat);
            return createSendMessage(chat.getId(), ENTER_PASSWORD);
        } else {
            return createSendMessage(chat.getId(), INCORRECT_EMAIL);
        }
    }

    private SendMessage fillInPassword(Chat chat, String messageText) {
        if (isValidPassword(messageText)) {
            UserDto userDto = chat.getUserDto();
            userDto.setPassword(messageText);
            try {
                bankAppServiceClient.saveNewClient(userDto);
                return createSendMessageWithButtons(chat.getId(), REGISTRATION_COMPLETED, List.of(REGISTER_USER, LOG_IN));
            } catch (ResponseStatusException exception) {
                return createSendMessageWithButtons(chat.getId(), String.format(REGISTRATION_FAILED, exception.getReason()),
                        List.of(REGISTER_USER, LOG_IN));
            } finally {
                chatRepository.delete(chat);
            }
        } else {
            return createSendMessage(chat.getId(), INCORRECT_PASSWORD);
        }
    }

    private boolean isValidName(String messageText) {
        return messageText.matches(NAME_PATTERN);
    }

    private boolean isValidPhone(String messageText) {
        return messageText.matches(PHONE_PATTERN);
    }

    private boolean isValidAddress(String messageText) {
        return messageText.matches(ADDRESS_PATTERN);
    }

    private boolean isValidEmail(String messageText) {
        return messageText.matches(EMAIL_PATTERN);
    }

    private boolean isValidPassword(String messageText) {
        return messageText.matches(PASSWORD_PATTERN);
    }
}
