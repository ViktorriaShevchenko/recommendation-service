package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RuleStatResponseDTO;
import com.starbank.recommendation_service.dto.RuleStatsResponse;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.repository.dynamic.RuleStatisticRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleStatisticServiceTest {

    @Mock
    private RuleStatisticRepository ruleStatisticRepository;

    @Mock
    private DynamicRuleRepository dynamicRuleRepository;

    @InjectMocks
    private RuleStatisticService ruleStatisticService;

    @Test
    void incrementStatistic_shouldCallRepositoryMethod() {
        // Arrange
        UUID ruleId = UUID.randomUUID();
        when(ruleStatisticRepository.incrementCountByRuleId(ruleId)).thenReturn(1);

        // Act
        ruleStatisticService.incrementStatistic(ruleId);

        // Assert
        verify(ruleStatisticRepository, times(1)).incrementCountByRuleId(ruleId);
    }

    @Test
    void getAllRulesStatistics_shouldReturnAllRulesWithStatistics() {
        // Arrange
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        DynamicRecommendationRule rule1 = new DynamicRecommendationRule();
        rule1.setId(ruleId1);
        rule1.setProductName("Product 1");

        DynamicRecommendationRule rule2 = new DynamicRecommendationRule();
        rule2.setId(ruleId2);
        rule2.setProductName("Product 2");

        List<DynamicRecommendationRule> allRules = Arrays.asList(rule1, rule2);

        when(dynamicRuleRepository.findAll()).thenReturn(allRules);
        when(ruleStatisticRepository.findByRuleId(ruleId1))
                .thenReturn(Optional.of(mockRuleStatistic(ruleId1, 5L)));
        when(ruleStatisticRepository.findByRuleId(ruleId2))
                .thenReturn(Optional.empty());  // Для второго правила статистики нет

        // Act
        RuleStatsResponse result = ruleStatisticService.getAllRulesStatistics();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getStats());
        assertEquals(2, result.getStats().size());

        // Проверяем первое правило (со статистикой)
        RuleStatResponseDTO stat1 = result.getStats().stream()
                .filter(s -> s.getRuleId().equals(ruleId1))
                .findFirst()
                .orElseThrow();
        assertEquals(5L, stat1.getCount());

        // Проверяем второе правило (без статистики - должно быть 0)
        RuleStatResponseDTO stat2 = result.getStats().stream()
                .filter(s -> s.getRuleId().equals(ruleId2))
                .findFirst()
                .orElseThrow();
        assertEquals(0L, stat2.getCount());
    }

    @Test
    void getAllRulesStatistics_whenRepositoryThrowsException_shouldHandleGracefully() {
        // Arrange
        UUID ruleId = UUID.randomUUID();
        DynamicRecommendationRule rule = new DynamicRecommendationRule();
        rule.setId(ruleId);

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(ruleStatisticRepository.findByRuleId(ruleId))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        RuleStatsResponse result = ruleStatisticService.getAllRulesStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getStats().size());
        assertEquals(0L, result.getStats().get(0).getCount());  // При ошибке возвращаем 0
    }

    private com.starbank.recommendation_service.entity.dynamic.RuleStatistic mockRuleStatistic(UUID ruleId, Long count) {
        com.starbank.recommendation_service.entity.dynamic.RuleStatistic statistic =
                new com.starbank.recommendation_service.entity.dynamic.RuleStatistic();
        statistic.setCount(count);
        return statistic;
    }
}
