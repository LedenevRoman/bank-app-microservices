package com.training.rledenev.service.action.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.entity.Chat;
import com.training.rledenev.enums.Role;
import com.training.rledenev.repository.ChatRepository;
import com.training.rledenev.service.action.ActionMessageHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.training.rledenev.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class AgreementMessageHandlerService implements ActionMessageHandlerService {
    private final BankAppServiceClient bankAppServiceClient;
    private final ChatRepository chatRepository;

    @Override
    public SendMessage handleMessage(Chat chat, String message, Role role) {
        long chatId = chat.getId();
        if (role != Role.MANAGER) {
            return createSendMessageWithButtons(chatId, ACCESS_DENIED, getListOfActionsByUserRole(role));
        }
        List<AgreementDto> agreementDtos = bankAppServiceClient.getAgreementsForManager();
        final Long agreementId;
        if (chat.getChosenAgreementId() == null) {
            if (message.equals(NEW_AGREEMENTS)) {
                return createSendMessageWithButtons(chatId, getListNewAgreementsMessage(agreementDtos),
                        getListOfAgreementsIdButtons(agreementDtos));
            }
            try {
                agreementId = Long.parseLong(message);
            } catch (NumberFormatException exception) {
                return createSendMessageWithButtons(chatId, INCORRECT_NUMBER_INT,
                        getListOfAgreementsIdButtons(agreementDtos));
            }
            return getAgreementIdSendMessage(chat, agreementDtos, agreementId);
        } else {
            agreementId = chat.getChosenAgreementId();
            agreementDtos.removeIf(a -> a.getId().equals(agreementId));
            chat.setChosenAgreementId(null);
            chatRepository.save(chat);
            if (message.equals(CONFIRM)) {
                bankAppServiceClient.confirmAgreementByManager(agreementId);
                return createSendMessageWithButtons(chatId,
                        getStatusMessageWithListAgreements(AGREEMENT_CONFIRMED, agreementDtos, agreementId),
                        getListOfAgreementsIdButtons(agreementDtos));
            }
            if (message.equals(BLOCK)) {
                bankAppServiceClient.blockAgreementByManager(agreementId);
                return createSendMessageWithButtons(chatId,
                        getStatusMessageWithListAgreements(AGREEMENT_BLOCKED, agreementDtos, agreementId),
                        getListOfAgreementsIdButtons(agreementDtos));
            }
        }
        return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, List.of(EXIT));
    }

    private SendMessage getAgreementIdSendMessage(Chat chat, List<AgreementDto> agreementDtos, Long agreementId) {
        Optional<AgreementDto> optionalAgreementDto = getOptionalFromListById(agreementDtos, agreementId);
        if (optionalAgreementDto.isPresent()) {
            AgreementDto agreementDto = optionalAgreementDto.get();
            chat.setChosenAgreementId(agreementId);
            chatRepository.save(chat);
            return createSendMessageWithButtons(chat.getId(), getSelectedAgreementMessage(agreementDto),
                    getConfirmBlockButtons());
        } else {
            return createSendMessageWithButtons(chat.getId(), WRONG_AGREEMENT_ID,
                    getListOfAgreementsIdButtons(agreementDtos));
        }
    }

    private static Optional<AgreementDto> getOptionalFromListById(List<AgreementDto> agreementDtos, long agreementId) {
        return agreementDtos.stream()
                .filter(a -> a.getId().equals(agreementId))
                .findFirst();
    }

    private String getStatusMessageWithListAgreements(String message, List<AgreementDto> agreementDtos,
                                                      Long agreementId) {
        return String.format(message + getListNewAgreementsMessage(agreementDtos), agreementId);
    }

    private String getSelectedAgreementMessage(AgreementDto agreementDto) {
        return String.format(SELECTED_AGREEMENT_INFO, agreementDto.getId(), agreementDto.getProductName(),
                agreementDto.getSum(), agreementDto.getCurrencyCode(),
                getStringFormattedPeriod(agreementDto.getPeriodMonths()));
    }

    private List<String> getListOfAgreementsIdButtons(List<AgreementDto> agreementDtos) {
        List<String> buttons = agreementDtos.stream()
                .map(agreementDto -> agreementDto.getId().toString())
                .collect(Collectors.toList());
        buttons.add(BACK);
        return buttons;
    }

    private String getListNewAgreementsMessage(List<AgreementDto> agreementDtos) {
        StringBuilder stringBuilder = new StringBuilder(NEW_AGREEMENTS_LIST);
        for (AgreementDto agreementDto : agreementDtos) {
            stringBuilder.append(String.format(AGREEMENT_INFO, agreementDto.getId(), agreementDto.getProductName(),
                            agreementDto.getSum(), agreementDto.getCurrencyCode(),
                            getStringFormattedPeriod(agreementDto.getPeriodMonths())))
                    .append("\n")
                    .append("\n");
        }
        stringBuilder.append("\n")
                .append(SELECT_AGREEMENT_ID);
        return stringBuilder.toString();
    }
}
