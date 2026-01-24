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

    public RecommendationService(List<RecommendationRuleSet> ruleSets,
                                 DynamicRuleRepository dynamicRuleRepository,
                                 RecommendationsRepository recommendationsRepository,
                                 DynamicRecommendationRepository dynamicRecommendationRepository) {
        this.ruleSets = ruleSets;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.dynamicRecommendationRepository = dynamicRecommendationRepository;
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
            boolean conditionResult = evaluateCondition(condition, userId);

            if (condition.isNegate()) {
                conditionResult = !conditionResult;
            }

            if (!conditionResult) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(RuleCondition condition, UUID userId) {
        String query = condition.getQuery();
        List<String> arguments = condition.getArguments();

        try {
            switch (query) {
                case "USER_OF":
                    return recommendationsRepository.hasProduct(userId,
                            ProductType.valueOf(arguments.get(0)));

                case "ACTIVE_USER_OF":
                    return recommendationsRepository.hasActiveProduct(userId,
                            ProductType.valueOf(arguments.get(0)));

                case "TRANSACTION_SUM_COMPARE":
                    validateArgumentsCount(arguments, 4, query);
                    int actualSum = recommendationsRepository.transactionSumAndTypeForProductType(
                            userId,
                            ProductType.valueOf(arguments.get(0)),
                            TransactionType.valueOf(arguments.get(1))
                    );
                    int requiredValue = Integer.parseInt(arguments.get(3));
                    String operator = arguments.get(2);
                    return compareWithOperator(actualSum, operator, requiredValue);

                case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                    validateArgumentsCount(arguments, 2, query);
                    int depositSum = recommendationsRepository.transactionSumAndTypeForProductType(
                            userId,
                            ProductType.valueOf(arguments.get(0)),
                            TransactionType.DEPOSIT
                    );
                    int withdrawSum = recommendationsRepository.transactionSumAndTypeForProductType(
                            userId,
                            ProductType.valueOf(arguments.get(0)),
                            TransactionType.WITHDRAW
                    );
                    return compareWithOperator(depositSum, arguments.get(1), withdrawSum);

                default:
                    throw new IllegalArgumentException("Unknown query type: " + query);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
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
