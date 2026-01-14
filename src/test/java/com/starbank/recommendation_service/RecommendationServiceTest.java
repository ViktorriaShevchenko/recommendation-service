package com.starbank.recommendation_service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.service.rule.RecommendationRuleSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRuleSet rule1;

    @Mock
    private RecommendationRuleSet rule2;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        // Перед каждым тестом создаем новый сервис со списком правил
        List<RecommendationRuleSet> ruleSets = Arrays.asList(rule1, rule2);
        recommendationService = new RecommendationService(ruleSets);
    }

    @Test
    void shouldReturnRecommendationsFromAllRules() {
        // Given
        UUID userId = UUID.randomUUID();
        RecommendationDTO dto1 = new RecommendationDTO(UUID.randomUUID(), "Test 1", "Text 1");
        RecommendationDTO dto2 = new RecommendationDTO(UUID.randomUUID(), "Test 2", "Text 2");

        when(rule1.check(userId)).thenReturn(Optional.of(dto1));
        when(rule2.check(userId)).thenReturn(Optional.of(dto2));

        // When
        RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);

        // Then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getRecommendations()).hasSize(2);
        assertThat(response.getRecommendations()).contains(dto1, dto2);
    }

    @Test
    void shouldReturnEmptyListWhenNoRulesMatch() {
        // Given
        UUID userId = UUID.randomUUID();
        when(rule1.check(userId)).thenReturn(Optional.empty());
        when(rule2.check(userId)).thenReturn(Optional.empty());

        // When
        RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);

        // Then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getRecommendations()).isEmpty();
    }

    @Test
    void shouldHandleEmptyRulesList() {
        // Given: Сервис с пустым списком правил
        recommendationService = new RecommendationService(Collections.emptyList());
        UUID userId = UUID.randomUUID();

        // When
        RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);

        // Then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getRecommendations()).isEmpty();
    }
}
