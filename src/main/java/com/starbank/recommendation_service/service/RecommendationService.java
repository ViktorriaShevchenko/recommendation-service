package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.DynamicRecommendationRepository;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import com.starbank.recommendation_service.service.rule.RecommendationRuleSet;
import com.starbank.recommendation_service.service.rule.condition.ConditionEvaluatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets; // Фиксированные правила
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RecommendationsRepository recommendationsRepository;
    private final DynamicRecommendationRepository dynamicRecommendationRepository;
    private final ConditionEvaluatorService conditionEvaluatorService;

    public RecommendationService(List<RecommendationRuleSet> ruleSets,
                                 DynamicRuleRepository dynamicRuleRepository,
                                 RecommendationsRepository recommendationsRepository,
                                 DynamicRecommendationRepository dynamicRecommendationRepository,
                                 ConditionEvaluatorService conditionEvaluatorService) {
        this.ruleSets = ruleSets;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.dynamicRecommendationRepository = dynamicRecommendationRepository;
        this.conditionEvaluatorService = conditionEvaluatorService;
    }

    @Transactional
    public RecommendationResponse getRecommendationsForUser(UUID userId) {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        // 1. Проверяем фиксированные правила
        if (ruleSets != null) {
            for (RecommendationRuleSet ruleSet : ruleSets) {
                ruleSet.check(userId).ifPresent(recommendation -> {
                    if (!dynamicRecommendationRepository.isAlreadyIssued(userId, recommendation.getId())) {
                        recommendations.add(recommendation);
                        dynamicRecommendationRepository.save(userId, recommendation.getId(),
                                recommendation.getName(), recommendation.getText());
                    }
                });
            }
        }

        // 2. Проверяем динамические правила
        List<DynamicRecommendationRule> allRules = dynamicRuleRepository.findAll();

        for (DynamicRecommendationRule rule : allRules) {
            if (isRuleApplicable(rule, userId)) {
                if (!dynamicRecommendationRepository.isAlreadyIssued(userId, rule.getProductId())) {
                    RecommendationDTO recommendation = convertToRecommendationDTO(rule);
                    recommendations.add(recommendation);
                    dynamicRecommendationRepository.save(userId, rule.getProductId(),
                            rule.getProductName(), rule.getProductText());
                }
            }
        }

        return new RecommendationResponse(userId, recommendations);
    }

    private boolean isRuleApplicable(DynamicRecommendationRule rule, UUID userId) {
        for (RuleCondition condition : rule.getRule()) {
            if (!evaluateCondition(condition, userId)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(RuleCondition condition, UUID userId) {
        return conditionEvaluatorService.evaluateCondition(condition, userId, recommendationsRepository);
    }

    private void validateArgumentsCount(List<String> arguments, int expected, String query) {
        if (arguments.size() != expected) {
            throw new IllegalArgumentException(
                    String.format("Query %s requires %d arguments, but got %d",
                            query, expected, arguments.size()));
        }
    }

    private boolean compareWithOperator(int actual, String operator, int expected) {
        switch (operator) {
            case ">":
                return actual > expected;
            case "<":
                return actual < expected;
            case "=":
                return actual == expected;
            case ">=":
                return actual >= expected;
            case "<=":
                return actual <= expected;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private RecommendationDTO convertToRecommendationDTO(DynamicRecommendationRule rule) {
        return new RecommendationDTO(
                rule.getProductId(),
                rule.getProductName(),
                rule.getProductText()
        );
    }
}
