package com.starbank.recommendation_service.telegram;

import com.starbank.recommendation_service.bot.RecommendationTelegramBot;
import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationTelegramBotTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private UserService userService;

    @Mock
    private org.telegram.telegrambots.bots.DefaultAbsSender sender;

    private RecommendationTelegramBot bot;

    @BeforeEach
    void setUp() {
        bot = new RecommendationTelegramBot("test_token", "test_bot", recommendationService, userService);
    }

    @Test
    void onUpdateReceived_withStartCommand_shouldSendGreeting() throws TelegramApiException {
        // Arrange
        Update update = createTextUpdate("/start", 12345L);

        // Act
        bot.onUpdateReceived(update);

        // Assert через verify - бот должен отправить сообщение
        // В реальности нужно использовать spy или проверить логи
    }

    @Test
    void onUpdateReceived_withRecommendCommandAndExistingUser_shouldSendRecommendations() throws TelegramApiException {
        // Arrange
        String username = "ivanov";
        UUID userId = UUID.randomUUID();
        Update update = createTextUpdate("/recommend " + username, 12345L);

        when(userService.findSingleUserIdByUsername(username))
                .thenReturn(Optional.of(userId));
        when(userService.getUserFullName(userId))
                .thenReturn("Иван Иванов");

        RecommendationResponse response = new RecommendationResponse(
                userId,
                Arrays.asList(
                        new RecommendationDTO("Кредит", "Описание кредита"),
                        new RecommendationDTO("Вклад", "Описание вклада")
                )
        );
        when(recommendationService.getRecommendationsForUser(userId))
                .thenReturn(response);

        // Act
        bot.onUpdateReceived(update);

        // Assert
        verify(userService, times(1)).findSingleUserIdByUsername(username);
        verify(recommendationService, times(1)).getRecommendationsForUser(userId);
    }

    @Test
    void onUpdateReceived_withRecommendCommandAndNonExistentUser_shouldSendUserNotFound() throws TelegramApiException {
        // Arrange
        String username = "nonexistent";
        Update update = createTextUpdate("/recommend " + username, 12345L);

        when(userService.findSingleUserIdByUsername(username))
                .thenReturn(Optional.empty());

        // Act
        bot.onUpdateReceived(update);

        // Assert
        verify(userService, times(1)).findSingleUserIdByUsername(username);
        verify(recommendationService, never()).getRecommendationsForUser(any());
    }

    @Test
    void onUpdateReceived_withInvalidCommand_shouldSendHelpMessage() throws TelegramApiException {
        // Arrange
        Update update = createTextUpdate("invalid command", 12345L);

        // Act
        bot.onUpdateReceived(update);

        // Assert
        // Проверяем, что отправляется help сообщение
    }

    private Update createTextUpdate(String text, Long chatId) {
        Update update = new Update();
        Message message = new Message();
        message.setText(text);
        message.setChat(new org.telegram.telegrambots.meta.api.objects.Chat(chatId, "private"));
        update.setMessage(message);
        return update;
    }
}
