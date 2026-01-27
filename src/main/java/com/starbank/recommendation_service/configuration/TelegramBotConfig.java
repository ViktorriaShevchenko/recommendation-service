package com.starbank.recommendation_service.configuration;

import com.starbank.recommendation_service.bot.RecommendationTelegramBot;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Bean
    public TelegramBotsApi telegramBotsApi(RecommendationTelegramBot bot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }

    @Bean
    public RecommendationTelegramBot recommendationBot(RecommendationService recommendationService,
                                                       UserService userService) {
        return new RecommendationTelegramBot(
                botToken,
                botUsername,
                recommendationService,
                userService
        );
    }
}
