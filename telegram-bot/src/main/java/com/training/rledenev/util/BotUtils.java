package com.training.rledenev.util;

import com.training.rledenev.enums.CurrencyCode;
import com.training.rledenev.enums.Role;
import com.training.rledenev.enums.TransactionType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class BotUtils {
    private static final String INTEREST_RATE_AND_PERIOD = """
            with an interest rate of %.2f%%
            for a period of %s.""";
    public static final String START = "/start";
    public static final String WELCOME_MESSAGE = "Welcome to banking application!";
    public static final String EXIT = "Exit";
    public static final String BACK = "Back";
    public static final String CONFIRM = "Confirm";
    public static final String CANCEL = "Cancel";
    public static final String REGISTER_USER = "Register";
    public static final String LOG_IN = "Log in";
    public static final String BLOCK = "Block";
    public static final String TRANSACTION_COMPLETED = "Transaction completed successfully";
    public static final String TRANSACTION_CANCELED = "Transaction was canceled";
    public static final String TRANSACTION_FAILED = "Transaction failed.\n";
    public static final String UNKNOWN_INPUT_MESSAGE = "Sorry, I don't know how to handle such command yet :(";
    public static final String ENTER_FIRST_NAME = "Please enter your first name:";
    public static final String ENTER_LAST_NAME = "Please enter your last name:";
    public static final String ENTER_PHONE = "Please enter your phone number:";
    public static final String ENTER_ADDRESS = "Please enter your address:";
    public static final String ENTER_EMAIL = "Please enter your email:";
    public static final String ENTER_PASSWORD = "Please enter your password:";
    public static final String INCORRECT_NAME = """
            The name must contain only letters of the English alphabet.
            Please enter correct name:""";
    public static final String INCORRECT_PHONE = """
            The phone number must starts with '+', and contain only numbers and hyphens.
            Please enter correct phone number:""";
    public static final String INCORRECT_ADDRESS = """
            Address is incorrect.
            Please, enter correct address:""";
    public static final String INCORRECT_EMAIL = """
            Email is incorrect.
                        
            Please, enter correct email:""";
    public static final String INCORRECT_PASSWORD = """
            The password is incorrect.
            Password is required to contain only English alphabet characters at least one uppercase and one lowercase, also one digit and one special character.
            Please, enter correct password:
            """;
    public static final String REGISTRATION_COMPLETED = "Registration is completed";
    public static final String REGISTRATION_FAILED = """
            Registration failed.
            %s
            Please, try again:""";
    public static final String AUTHENTICATION_FAILED = """
            Email or password is incorrect.
            Please, try again:""";
    public static final String SELECT_ACTION = "Select action:";
    public static final String AUTHENTICATION_COMPLETED = """
            Good afternoon %s %s!
            """ + SELECT_ACTION;
    public static final String SESSION_CLOSED = "Session was expired, please log in";
    public static final String PRODUCTS = "Products";
    public static final String CURRENCY_RATES = "Currency rates";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String NEW_AGREEMENTS = "New agreements";
    public static final String MY_ACCOUNTS = "My accounts";
    public static final String MY_ACCOUNTS_LIST = "Here is a list of your accounts:\n";
    public static final String SHORT_ACCOUNT_INFO = "%d) Number: %s, product name: %s, balance: %.2f %s.";
    public static final String FULL_ACCOUNT_INFO = """
            Account number %s info:
            Owner: %s
            Product name: %s
            Interest rate: %.2f
            Start date by agreement: %td-%tm-%tY
            Payment term by agreement: %td-%tm-%tY
            Balance: %.2f
            Currency: %s
                        
            """ + SELECT_ACTION;
    public static final String TRANSACTION_INFO = """
            Your transaction:
            To account: %s
            Amount: %.2f
            Currency code: %s
            Type: %s
            Description: %s""";
    public static final String SELECT_ACCOUNT = "Please, select account:";
    public static final String MAKE_TRANSACTION = "Make transaction";
    public static final String ENTER_ACCOUNT_NUMBER = "Enter the account number to which the transfer should be made:";
    public static final String SELECT_TYPE = "Select type:";
    public static final String ENTER_DESCRIPTION = "Enter description:";
    public static final String LIST_TRANSACTIONS = "Here is a list of your transactions:\n";
    public static final String AMOUNT_DEBIT_TRANSACTION_INFO = "- %.2f %s (%.2f %s), on account: %s.\n";
    public static final String AMOUNT_IN_SAME_CURRENCY_DEBIT_TRANSACTION_INFO = "- %.2f %s, on account: %s.\n";
    public static final String AMOUNT_CREDIT_TRANSACTION_INFO = "+ %.2f %s (%.2f %s), from account: %s.\n";
    public static final String AMOUNT_IN_SAME_CURRENCY_CREDIT_TRANSACTION_INFO = "+ %.2f %s, from account: %s.\n";
    public static final String ANOTHER_TRANSACTION_INFO = """
            Date: %tc,
            with type: %s, and description: %s.
                        
            """;
    public static final String VIEW_ALL_TRANSACTIONS = "View all transactions";
    public static final String BACK_TO_LIST_ACCOUNTS = "Back to list of accounts";
    public static final String NEW_AGREEMENTS_LIST = "Here is a list of new agreements:\n";
    public static final String AGREEMENT_INFO = "- ID: %d, product name: %s, sum: %.2f %s, period: %s.";
    public static final String SELECTED_AGREEMENT_INFO = "You have chosen the following agreement:\n"
            + AGREEMENT_INFO + "\n\n"
            + SELECT_ACTION;
    public static final String SELECT_AGREEMENT_ID = "Please, select agreement id to approve:";
    public static final String WRONG_AGREEMENT_ID = "Wrong agreement id number.\n"
            + SELECT_AGREEMENT_ID;
    public static final String AGREEMENT_CONFIRMED = "Agreement with id %d was confirmed.\n\n";
    public static final String AGREEMENT_BLOCKED = "Agreement with id %d was blocked.\n\n";
    public static final String PRODUCTS_LIST_MESSAGE = "Here is a list of available types of products:\n";
    public static final String PRODUCTS_LIST_OF_TYPE = "Here is a list of products of type %s:\n";
    public static final String PRODUCT_INFO = ". %s, from zł.%,d\n"
            + INTEREST_RATE_AND_PERIOD;
    public static final String SELECT_PRODUCT = "Select your desired product:";
    public static final String SELECT_CURRENCY = "Select currency:";
    public static final String NOTE_ABOUT_CONVERT = "Note that the amount will be converted into PLN to compare" +
            " with the limit for your product.";
    public static final String ENTER_AMOUNT = "Please enter the desired amount of money in your currency:";
    public static final String INCORRECT_NUMBER = """
            Number must contain only numbers and one dot.
            Please enter correct number:""";
    public static final String INCORRECT_NUMBER_INT = """
            Number must contain only numbers.
            Please enter correct number:""";
    public static final String WRONG_ACCOUNT_INDEX = """
            Wrong account index.
            Please enter the index listed on the buttons below:""";
    public static final String SUITABLE_PRODUCT = "The product that suits your needs is a %s.\n"
            + INTEREST_RATE_AND_PERIOD;
    public static final String AGREEMENT_DONE = """
            Done. You took the following product:
            %s
            on %,d %s
            """ + INTEREST_RATE_AND_PERIOD
            + "\n\nPlease wait until the manager approves your application.\n\n"
            + SELECT_ACTION;

    public static final String OFFICIAL_CURRENCY_RATE = """
            Official exchange rate of PLN to %s
            on date: %td-%tm-%tY
            is: %.4f PLN for 1 %s.
                        
            """ + SELECT_CURRENCY;
    public static final String UNKNOWN_CURRENCY_CODE = "Unknown currency code.\n\n"
            + SELECT_CURRENCY;
    private static final int MAX_ROW_SIZE_IN_KEYBOARD = 4;

    private BotUtils() {
    }

    public static SendMessage createSendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        return sendMessage;
    }

    public static SendMessage createSendMessageWithButtons(Long chatId, String textToSend, List<String> buttons) {
        SendMessage sendMessage = createSendMessage(chatId, textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = getKeyboardRows(buttons);

        keyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public static List<String> getListOfActionsByUserRole(Role role) {
        List<String> actions = new ArrayList<>();
        if (role == Role.MANAGER) {
            actions.add(NEW_AGREEMENTS);
        }
        actions.add(MY_ACCOUNTS);
        actions.add(PRODUCTS);
        actions.add(CURRENCY_RATES);
        actions.add(EXIT);
        return actions;
    }

    public static String getStringFormattedPeriod(Integer periodMonths) {
        int years = periodMonths / 12;
        int remainingMonths = periodMonths % 12;

        String result = "";
        if (years > 0) {
            result += years + " " + (years == 1 ? "year" : "years");
        }
        if (remainingMonths > 0) {
            if (!result.isEmpty()) {
                result += " ";
            }
            result += remainingMonths + " " + (remainingMonths == 1 ? "month" : "months");
        }

        return result;
    }

    public static List<String> getCurrencyButtons() {
        List<String> currencies = Arrays.stream(CurrencyCode.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        currencies.add(BACK);
        return currencies;
    }

    public static List<String> getConfirmBlockButtons() {
        return List.of(CONFIRM, BLOCK, BACK);
    }

    public static List<String> getListOfActionsForClientAccount() {
        return List.of(MAKE_TRANSACTION, VIEW_ALL_TRANSACTIONS, BACK_TO_LIST_ACCOUNTS);
    }

    public static List<String> getTypeButtons() {
        TransactionType[] types = TransactionType.values();
        return Arrays.stream(types).map(TransactionType::getSimpleName)
                .toList();
    }

    private static List<KeyboardRow> getKeyboardRows(List<String> buttons) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        int rowLength = 0;
        int i = 0;
        KeyboardRow row = new KeyboardRow();
        while (i < buttons.size()) {
            row.add(new KeyboardButton(buttons.get(i)));
            rowLength++;
            if (rowLength == MAX_ROW_SIZE_IN_KEYBOARD || i == buttons.size() - 1) {
                keyboard.add(row);
                row = new KeyboardRow();
                rowLength = 0;
            }
            i++;
        }
        return keyboard;
    }
}
