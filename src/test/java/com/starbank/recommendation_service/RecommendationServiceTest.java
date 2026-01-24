package com.starbank.recommendation_service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.DynamicRecommendationRepository;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.service.rule.RecommendationRuleSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationService Unit Tests")
class RecommendationServiceTest {

    // Используем Spy для списка или создаем реальный список
    private List<RecommendationRuleSet> ruleSets = new ArrayList<>();

    @Mock
    private DynamicRuleRepository dynamicRuleRepository;

    @Mock
    private RecommendationsRepository recommendationsRepository;

    @Mock
    private DynamicRecommendationRepository futureRepository;

    @Mock
    private RecommendationRuleSet mockRuleSet;

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        // Добавляем мок в список
        ruleSets.clear();
        ruleSets.add(mockRuleSet);

        service = new RecommendationService(
                ruleSets,
                dynamicRuleRepository,
                recommendationsRepository,
                futureRepository
        );
    }

    // ==================== ТЕСТЫ ДЛЯ evaluateCondition ЧЕРЕЗ REFLECTION ====================

    @Test
    @DisplayName("evaluateCondition - USER_OF возвращает true когда пользователь имеет продукт")
    void evaluateCondition_UserOf_ReturnsTrue() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        RuleCondition condition = new RuleCondition();
        condition.setQuery("USER_OF");
        condition.setArguments(List.of("DEBIT"));
        condition.setNegate(false);

        when(recommendationsRepository.hasProduct(userId, ProductType.DEBIT))
                .thenReturn(true);

        // Act
        boolean result = invokePrivateEvaluateCondition(condition, userId);

        // Assert
        assertTrue(result);
        verify(recommendationsRepository).hasProduct(userId, ProductType.DEBIT);
    }

    @Test
    @DisplayName("evaluateCondition - USER_OF с negate возвращает false когда пользователь имеет продукт")
    void evaluateCondition_UserOfWithNegate_ReturnsFalse() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        RuleCondition condition = new RuleCondition();
        condition.setQuery("USER_OF");
        condition.setArguments(List.of("DEBIT"));
        condition.setNegate(true); // negate = true

        when(recommendationsRepository.hasProduct(userId, ProductType.DEBIT))
                .thenReturn(true);

        // Act
        boolean result = invokePrivateEvaluateCondition(condition, userId);

        // Assert
        // Если negate = true, то результат должен быть обратным
        // USER_OF = true, но negate = true, значит должно быть false
        assertFalse(result);
    }

    @Test
    @DisplayName("evaluateCondition - USER_OF с negate возвращает true когда пользователь НЕ имеет продукт")
    void evaluateCondition_UserOfWithNegate_WhenUserDoesNotHaveProduct_ReturnsTrue() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        RuleCondition condition = new RuleCondition();
        condition.setQuery("USER_OF");
        condition.setArguments(List.of("DEBIT"));
        condition.setNegate(true); // negate = true

        when(recommendationsRepository.hasProduct(userId, ProductType.DEBIT))
                .thenReturn(false); // Пользователь НЕ имеет продукт

        // Act
        boolean result = invokePrivateEvaluateCondition(condition, userId);

        // Assert
        // USER_OF = false, но negate = true, значит должно быть true
        assertTrue(result);
    }

    // ==================== ТЕСТЫ ДЛЯ getRecommendationsForUser ====================

    @Test
    @DisplayName("getRecommendationsForUser - возвращает пустой список для нового пользователя")
    void getRecommendationsForUser_NewUser_ReturnsEmptyList() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Настраиваем моки
        when(mockRuleSet.check(userId)).thenReturn(Optional.empty());
        when(dynamicRuleRepository.findAll()).thenReturn(List.of());

        // Act
        RecommendationResponse response = service.getRecommendationsForUser(userId);

        // Assert
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getRecommendations()).isEmpty();

        // Проверяем, что isAlreadyIssued НЕ вызывался, так как нет рекомендаций
        verify(futureRepository, never()).isAlreadyIssued(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("getRecommendationsForUser - возвращает рекомендацию из фиксированных правил")
    void getRecommendationsForUser_ReturnsFixedRuleRecommendation() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID recommendationId = UUID.randomUUID();
        RecommendationDTO recommendation = new RecommendationDTO(
                recommendationId, "Test Recommendation", "Test text");

        when(mockRuleSet.check(userId)).thenReturn(Optional.of(recommendation));
        when(dynamicRuleRepository.findAll()).thenReturn(List.of());
        when(futureRepository.isAlreadyIssued(userId, recommendationId))
                .thenReturn(false); // Рекомендация еще не выдана

        // Act
        RecommendationResponse response = service.getRecommendationsForUser(userId);

        // Assert
        assertThat(response.getRecommendations()).hasSize(1);
        assertThat(response.getRecommendations().get(0).getId()).isEqualTo(recommendationId);

        // Проверяем, что сохранили выдачу
        verify(futureRepository).isAlreadyIssued(userId, recommendationId);
        verify(futureRepository).save(
                eq(userId),
                eq(recommendationId),
                eq("Test Recommendation"),
                eq("Test text")
        );
    }

    @Test
    @DisplayName("getRecommendationsForUser - не возвращает уже выданную рекомендацию")
    void getRecommendationsForUser_DoesNotReturnAlreadyIssuedRecommendation() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID recommendationId = UUID.randomUUID();
        RecommendationDTO recommendation = new RecommendationDTO(
                recommendationId, "Test Recommendation", "Test text");

        when(mockRuleSet.check(userId)).thenReturn(Optional.of(recommendation));
        when(dynamicRuleRepository.findAll()).thenReturn(List.of());
        when(futureRepository.isAlreadyIssued(userId, recommendationId))
                .thenReturn(true); // Уже выдана!

        // Act
        RecommendationResponse response = service.getRecommendationsForUser(userId);

        // Assert
        assertThat(response.getRecommendations()).isEmpty();
        verify(futureRepository).isAlreadyIssued(userId, recommendationId);
        verify(futureRepository, never()).save(any(), any(), anyString(), anyString());
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ REFLECTION ====================

    /**
     * Вызывает приватный метод evaluateCondition через reflection
     */
    private boolean invokePrivateEvaluateCondition(RuleCondition condition, UUID userId)
            throws Exception {
        Method method = RecommendationService.class.getDeclaredMethod(
                "evaluateCondition", RuleCondition.class, UUID.class);
        method.setAccessible(true);
        return (boolean) method.invoke(service, condition, userId);
    }
}
