package com.training.rledenev.service.action.impl;

import com.training.rledenev.client.BankAppServiceClient;
import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.dto.ProductDto;
import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.ProductType;
import com.training.rledenev.enums.Role;
import com.training.rledenev.service.action.ActionMessageHandlerService;
import com.training.rledenev.service.chatmaps.ChatIdActionNameMap;
import com.training.rledenev.service.chatmaps.ChatIdAgreementDtoMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.training.rledenev.service.util.BotUtils.*;

@RequiredArgsConstructor
@Service
public class ProductMessageHandlerService implements ActionMessageHandlerService {
    private final BankAppServiceClient bankAppServiceClient;

    @Override
    public SendMessage handleMessage(long chatId, String message, Role role) {
        if (ChatIdAgreementDtoMap.get(chatId) == null) {
            return handleInitialProductActionMessage(chatId, message);
        } else {
            return handleAgreementCreationMessage(chatId, message, role);
        }
    }

    private SendMessage handleInitialProductActionMessage(long chatId, String message) {
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
            ChatIdAgreementDtoMap.put(chatId, agreementDto);
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

    private SendMessage handleAgreementCreationMessage(long chatId, String message, Role role) {
        AgreementDto agreementDto = ChatIdAgreementDtoMap.get(chatId);
        if (agreementDto.getCurrencyCode() == null) {
            agreementDto.setCurrencyCode(CurrencyCode.valueOf(message.toUpperCase()));
            if (isProductCard(agreementDto)) {
                return completeAgreementDtoForCardProductMessage(chatId, agreementDto);
            } else {
                return createSendMessage(chatId, ENTER_AMOUNT);
            }
        }
        if (agreementDto.getSum() == null) {
            try {
                agreementDto.setSum(BigDecimal.valueOf(Double.parseDouble(message)));
                return completeAgreementDtoMessage(chatId, role, agreementDto);
            } catch (NumberFormatException e) {
                return createSendMessage(chatId, INCORRECT_NUMBER);
            }
        } else {
            return createNewAgreement(chatId, role, agreementDto);
        }
    }

    private static boolean isProductCard(AgreementDto agreementDto) {
        return agreementDto.getProductType() == ProductType.DEBIT_CARD
                || agreementDto.getProductType() == ProductType.CREDIT_CARD;
    }

    private SendMessage completeAgreementDtoForCardProductMessage(long chatId, AgreementDto agreementDto) {
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
        return createSendMessageWithButtons(chatId,
                String.format(SUITABLE_PRODUCT, agreementDto.getProductName(), agreementDto.getInterestRate(),
                        getStringFormattedPeriod(agreementDto.getPeriodMonths())),
                List.of(CONFIRM, BACK));
    }

    private SendMessage completeAgreementDtoMessage(long chatId, Role role, AgreementDto agreementDto) {
        try {
            ProductDto productDto = bankAppServiceClient.getSuitableProduct(agreementDto.getProductType(),
                    agreementDto.getSum(), agreementDto.getCurrencyCode());
            agreementDto.setPeriodMonths(productDto.getPeriodMonths());
            agreementDto.setProductName(productDto.getName());
            return createSendMessageWithButtons(chatId,
                    String.format(SUITABLE_PRODUCT, agreementDto.getProductName(), productDto.getInterestRate(),
                            getStringFormattedPeriod(productDto.getPeriodMonths())),
                    List.of(CONFIRM, BACK));
        } catch (ResponseStatusException exception) {
            ChatIdAgreementDtoMap.remove(chatId);
            ChatIdActionNameMap.remove(chatId);
            return createSendMessageWithButtons(chatId, exception.getReason() + "\n" + SELECT_ACTION,
                    getListOfActionsByUserRole(role));
        }
    }

    private SendMessage createNewAgreement(long chatId, Role role, AgreementDto agreementDto) {
        agreementDto = bankAppServiceClient.createNewAgreement(agreementDto);
        ChatIdAgreementDtoMap.remove(chatId);
        ChatIdActionNameMap.remove(chatId);
        return createSendMessageWithButtons(chatId, getNewAgreementMessage(agreementDto),
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
