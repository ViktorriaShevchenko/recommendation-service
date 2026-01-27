package com.starbank.recommendation_service.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class RecommendationTelegramBot  extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;

    public RecommendationTelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            Long chatId = message.getChatId();

            if (text.equals("/start")) {
                sendGreetingMessage(chatId);
            } else {
                sendHelpMessage(chatId);
            }
        }
    }

    private void sendGreetingMessage(Long chatId) {
        String greetingText = """
                Здравствуйте! Я смогу порекомендовать Вам банковские продукты!
                Они обязательно Вас заинтересуют
                
                Для получения рекомендации введите
                /recommend имя_пользователя
                Пример:
                /recommend Иван Иванов
                """;
        sendMessage(chatId, greetingText);
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = """
                Неверная команда!
                
                Доступные команды:
                /start - показать приветствие
                /recommend имя_пользователя - получить рекомендации
                
                Пример:
                /recommend Иван Иванов
                """;
        sendMessage(chatId, helpText);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}