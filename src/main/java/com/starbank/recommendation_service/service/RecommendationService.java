package com.starbank.recommendation_service.service;
/*
import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public RecommendationResponse getRecommendationsForUser(UUID userId) {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (RecommendationRuleSet ruleSet : ruleSets) {
            ruleSet.check(userId)
                    .ifPresent(recommendations::add);
        }

        return new RecommendationResponse(userId, recommendations);
    }
}*/
import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final DynamicRuleRepository dynamicRuleRepository;
    private final RecommendationsRepository recommendationsRepository;

    public RecommendationResponse getRecommendationsForUser(UUID userId) {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        List<DynamicRecommendationRule> allRules = dynamicRuleRepository.findAll();

        for (DynamicRecommendationRule rule : allRules) {
            if (isRuleApplicable(rule, userId)) {

                recommendations.add(convertToRecommendationDTO(rule));
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

        switch (query) {
            case "USER_OF":
                return recommendationsRepository.hasProduct(userId,
                        ProductType.valueOf(arguments.get(0)));

            case "ACTIVE_USER_OF":
                return recommendationsRepository.hasActiveProduct(userId,
                        ProductType.valueOf(arguments.get(0)));

            case "TRANSACTION_SUM_COMPARE":
                int actualSum = recommendationsRepository.transactionSumAndTypeForProductType(
                        userId,
                        ProductType.valueOf(arguments.get(0)),
                        TransactionType.valueOf(arguments.get(1))
                );
                int requiredValue = Integer.parseInt(arguments.get(3));
                return compareWithOperator(actualSum, arguments.get(2), requiredValue);

            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
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
    }

    private boolean compareWithOperator(int actual, String operator, int expected) {
        return switch (operator) {
            case ">" -> actual > expected;
            case "<" -> actual < expected;
            case "=" -> actual == expected;
            case ">=" -> actual >= expected;
            case "<=" -> actual <= expected;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private RecommendationDTO convertToRecommendationDTO(DynamicRecommendationRule rule) {
        return new RecommendationDTO(
                rule.getProductId(),
                rule.getProductName(),
                rule.getProductText()
        );
    }
}
