package com.training.rledenev.service.action.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.dto.ProductDto;
import com.training.rledenev.entity.Chat;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.enums.Role;
import com.training.rledenev.repository.ChatRepository;
import com.training.rledenev.service.action.ActionMessageHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.training.rledenev.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class ProductMessageHandlerService implements ActionMessageHandlerService {
    private final BankAppServiceClient bankAppServiceClient;
    private final ChatRepository chatRepository;

    @Override
    public SendMessage handleMessage(Chat chat, String message, Role role) {
        if (chat.getAgreementDto() == null) {
            return handleInitialProductActionMessage(chat, message);
        } else {
            return handleAgreementCreationMessage(chat, message, role);
        }
    }

    private SendMessage handleInitialProductActionMessage(Chat chat, String message) {
        long chatId = chat.getId();
        List<ProductDto> productDtos = bankAppServiceClient.getAllActiveProductDtos();
        List<String> productTypes = productDtos.stream()
                .map(productDto -> productDto.getType().getSimpleName())
                .distinct()
                .collect(Collectors.toList());
        if (message.equals(PRODUCTS)) {
            return createSendMessageWithButtons(chatId, getResponse(productTypes), productTypes);
        }
        if (productTypes.contains(message)) {
            AgreementDto agreementDto = new AgreementDto();
            agreementDto.setProductType(ProductType.valueOf(message.toUpperCase().replaceAll("\\s", "_")));
            chat.setAgreementDto(agreementDto);
            chatRepository.save(chat);
            return createSendMessageWithButtons(
                    chatId,
                    getAllProductsWithTypeListMessage(message,
                            bankAppServiceClient.getActiveProductsWithType(agreementDto.getProductType())
                    ),
                    getCurrencyButtons()
            );
        }
        return createSendMessageWithButtons(chatId, UNKNOWN_INPUT_MESSAGE, List.of(EXIT));
    }

    private static String getResponse(List<String> productTypes) {
        StringBuilder stringBuilder = new StringBuilder(PRODUCTS_LIST_MESSAGE);
        productTypes.forEach(productType -> stringBuilder.append(productType).append("\n"));
        stringBuilder.append("\n").append(SELECT_PRODUCT);
        productTypes.add(BACK);
        return stringBuilder.toString();
    }

    private SendMessage handleAgreementCreationMessage(Chat chat, String message, Role role) {
        long chatId = chat.getId();
        AgreementDto agreementDto = chat.getAgreementDto();
        if (agreementDto.getCurrencyCode() == null) {
            agreementDto.setCurrencyCode(CurrencyCode.valueOf(message.toUpperCase()));
            chatRepository.save(chat);
            if (isProductCard(agreementDto)) {
                return fillInAgreementDtoForCardProduct(chat, agreementDto);
            } else {
                return createSendMessage(chatId, ENTER_AMOUNT);
            }
        }
        if (agreementDto.getSum() == null) {
            try {
                agreementDto.setSum(BigDecimal.valueOf(Double.parseDouble(message)));
                chatRepository.save(chat);
                return completeAgreementDtoMessage(chat, role, agreementDto);
            } catch (NumberFormatException e) {
                return createSendMessage(chatId, INCORRECT_NUMBER);
            }
        } else {
            return createNewAgreement(chat, role, agreementDto);
        }
    }

    private static boolean isProductCard(AgreementDto agreementDto) {
        return agreementDto.getProductType() == ProductType.DEBIT_CARD
                || agreementDto.getProductType() == ProductType.CREDIT_CARD;
    }

    private SendMessage fillInAgreementDtoForCardProduct(Chat chat, AgreementDto agreementDto) {
        long chatId = chat.getId();
        ProductDto productDto = bankAppServiceClient.getSuitableProduct(agreementDto.getProductType(),
                agreementDto.getSum(), agreementDto.getCurrencyCode());
        if (productDto == null) {
            return createSendMessageWithButtons(chatId, "No suitable product",
                    List.of(BACK));
        }
        BigDecimal amount = BigDecimal.valueOf(productDto.getMinLimit())
                .divide(bankAppServiceClient.getRateOfCurrency(agreementDto.getCurrencyCode().toString()), 2,
                        RoundingMode.HALF_UP);
        agreementDto.setPeriodMonths(productDto.getPeriodMonths());
        agreementDto.setSum(amount);
        agreementDto.setProductName(productDto.getName());
        agreementDto.setInterestRate(productDto.getInterestRate());
        chatRepository.save(chat);
        return createSendMessageWithButtons(chatId,
                String.format(SUITABLE_PRODUCT, agreementDto.getProductName(), agreementDto.getInterestRate(),
                        getStringFormattedPeriod(agreementDto.getPeriodMonths())),
                List.of(CONFIRM, BACK));
    }

    private SendMessage completeAgreementDtoMessage(Chat chat, Role role, AgreementDto agreementDto) {
        long chatId = chat.getId();
        try {
            ProductDto productDto = bankAppServiceClient.getSuitableProduct(agreementDto.getProductType(),
                    agreementDto.getSum(), agreementDto.getCurrencyCode());
            agreementDto.setPeriodMonths(productDto.getPeriodMonths());
            agreementDto.setProductName(productDto.getName());
            chatRepository.save(chat);
            return createSendMessageWithButtons(chatId,
                    String.format(SUITABLE_PRODUCT, agreementDto.getProductName(), productDto.getInterestRate(),
                            getStringFormattedPeriod(productDto.getPeriodMonths())),
                    List.of(CONFIRM, BACK));
        } catch (ResponseStatusException exception) {
            chat.setAgreementDto(null);
            chat.setActionName(null);
            chatRepository.save(chat);
            return createSendMessageWithButtons(chatId, exception.getReason() + "\n" + SELECT_ACTION,
                    getListOfActionsByUserRole(role));
        }
    }

    private SendMessage createNewAgreement(Chat chat, Role role, AgreementDto agreementDto) {
        agreementDto = bankAppServiceClient.createNewAgreement(agreementDto);
        chat.setAgreementDto(null);
        chat.setActionName(null);
        chatRepository.save(chat);
        return createSendMessageWithButtons(chat.getId(), getNewAgreementMessage(agreementDto),
                getListOfActionsByUserRole(role));
    }

    private String getNewAgreementMessage(AgreementDto agreementDto) {
        return String.format(AGREEMENT_DONE, agreementDto.getProductName(), Math.round(agreementDto.getSum().doubleValue()),
                agreementDto.getCurrencyCode(), agreementDto.getInterestRate(),
                getStringFormattedPeriod(agreementDto.getPeriodMonths()));
    }

    private static String getAllProductsWithTypeListMessage(String productType, List<ProductDto> allProductsWithType) {
        StringBuilder stringBuilder = new StringBuilder(String.format(PRODUCTS_LIST_OF_TYPE, productType));
        for (int i = 0; i < allProductsWithType.size(); i++) {
            ProductDto productDto = allProductsWithType.get(i);
            stringBuilder.append(i + 1)
                    .append(String.format(PRODUCT_INFO, productDto.getName(), productDto.getMinLimit(),
                            productDto.getInterestRate(), getStringFormattedPeriod(productDto.getPeriodMonths())))
                    .append("\n")
                    .append("\n");
        }
        stringBuilder.append("\n")
                .append(SELECT_CURRENCY)
                .append("\n")
                .append(NOTE_ABOUT_CONVERT);
        return stringBuilder.toString();
    }
}
