package com.starbank.recommendation_service;

import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.dto.dynamic.DynamicRuleRequest;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.FutureRepository;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.service.dynamic.DynamicRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TwoDatabasesIntegrationTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private DynamicRuleService dynamicRuleService;

    @Autowired
    private DynamicRuleRepository dynamicRuleRepository;

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем ТОЛЬКО основную БД (issued_recommendations не трогаем!)
        jdbcTemplate.execute("DELETE FROM transactions");
        jdbcTemplate.execute("DELETE FROM products");
        jdbcTemplate.execute("DELETE FROM users");

        // Очищаем динамические правила
        dynamicRuleRepository.deleteAll();
    }

    @Test
    void testDynamicRuleWorksAlone() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // 1. Создаем пользователя
        jdbcTemplate.execute("INSERT INTO users (id, name) VALUES ('" + userId + "', 'Test User')");

        // 2. Создаем DEBIT продукт
        jdbcTemplate.execute("INSERT INTO products (id, type, name) VALUES ('prod-debit', 'DEBIT', 'Дебетовая карта')");

        // 3. Мало транзакций (фиксированные правила НЕ сработают!)
        jdbcTemplate.execute("INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES " +
                "('txn-1', '" + userId + "', 'prod-debit', 'DEPOSIT', 1000)");

        // 4. Создаем динамическое правило
        DynamicRuleRequest request = new DynamicRuleRequest();
        request.setProductName("Dynamic Product");
        request.setProductId(productId);
        request.setProductText("Рекомендация");

        RuleCondition condition = new RuleCondition();
        condition.setQuery("USER_OF");
        condition.setArguments(List.of("DEBIT"));
        condition.setNegate(false);

        request.setRule(List.of(condition));

        dynamicRuleService.createRule(request);

        // Act
        RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);

        // Assert
        assertEquals(1, response.getRecommendations().size(),
                "Должна быть ОДНА рекомендация");
        assertEquals(productId, response.getRecommendations().get(0).getId());
    }

    @Test
    void testNoRecommendations() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID dynamicProductId = UUID.randomUUID();

        jdbcTemplate.execute("INSERT INTO users (id, name) VALUES ('" + userId + "', 'Empty User')");

        DynamicRuleRequest request = new DynamicRuleRequest();
        request.setProductName("Dynamic Product");
        request.setProductId(dynamicProductId);
        request.setProductText("Не появится");

        RuleCondition condition = new RuleCondition();
        condition.setQuery("USER_OF");
        condition.setArguments(List.of("INVEST")); // У пользователя нет INVEST
        condition.setNegate(false);
        request.setRule(List.of(condition));

        dynamicRuleService.createRule(request);

        // Act
        RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);

        // Assert
        assertEquals(0, response.getRecommendations().size());
    }
}
