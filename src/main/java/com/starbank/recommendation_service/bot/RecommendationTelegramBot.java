package com.starbank.recommendation_service.bot;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.UUID;

public class RecommendationTelegramBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final RecommendationService recommendationService;
    private final UserService userService;

    public RecommendationTelegramBot(String botToken, String botUsername,
                                     RecommendationService recommendationService,
                                     UserService userService) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.recommendationService = recommendationService;
        this.userService = userService;
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
            } else if (text.startsWith("/recommend ")) {
                handleRecommendCommand(chatId, text);
            } else {
                sendHelpMessage(chatId);
            }
        }
    }

    private void handleRecommendCommand(Long chatId, String command) {
        try {
            String[] parts = command.split(" ", 2);
            if (parts.length < 2) {
                sendMessage(chatId, "Введите команду и имя в формате: \n /recommend имя_пользователя");
                return;
            }

            String username = parts[1].trim();

            Optional<UUID> userIdOpt = userService.findSingleUserIdByUsername(username);

            if (userIdOpt.isEmpty()) {
                sendMessage(chatId, "Пользователь не найден");
                return;
            }

            UUID userId = userIdOpt.get();
            RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);

            String userInfo = userService.getUserFullName(userId);
            StringBuilder messageText = new StringBuilder();

            if (userInfo != null && !userInfo.trim().isEmpty()) {
                messageText.append("Здравствуйте, ").append(userInfo).append("!\n\n");
            } else {
                messageText.append("Здравствуйте!\n\n");
            }

            messageText.append("Новые продукты для вас:\n");

            if (response.getRecommendations().isEmpty()) {
                messageText.append("Для Вас пока нет подходящих рекомендаций. Загляните к нам попозже.");
            } else {
                int counter = 1;
                for (RecommendationDTO recommendation : response.getRecommendations()) {
                    messageText.append(counter).append(". ")
                            .append(recommendation.getName()).append("\n")
                            .append("   ").append(recommendation.getText()).append("\n\n");
                    counter++;
                }
            }

            sendMessage(chatId, messageText.toString());

        } catch (Exception e) {
            sendMessage(chatId, "Произошла ошибка при обработке запроса");
        }
    }

    private void sendGreetingMessage(Long chatId) {
        String greetingText = """
                Здравствуйте! Я бот, который сможет порекомендовать Вам банковские продукты!
                Они обязательно Вас заинтересуют!
                
                Для получения рекомендации введите
                /recommend имя_пользователя
                Пример:
                /recommend ivanov
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
                /recommend ivanov
                
                Имя пользователя должно быть без пробелов
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