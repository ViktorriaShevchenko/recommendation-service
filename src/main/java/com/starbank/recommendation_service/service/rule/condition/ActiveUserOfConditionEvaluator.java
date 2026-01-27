package com.starbank.recommendation_service.service.rule.condition;

import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ActiveUserOfConditionEvaluator implements ConditionEvaluator {
    @Override
    public boolean supports(String queryType) {
        return "ACTIVE_USER_OF".equals(queryType);
    }

    @Override
    public boolean evaluate(RuleCondition condition, UUID userId, RecommendationsRepository repository) {
        validateArgumentsCount(condition.getArguments(), 1, "ACTIVE_USER_OF");
        return repository.hasActiveProduct(userId,
                ProductType.valueOf(condition.getArguments().get(0)));
    }

    private void validateArgumentsCount(java.util.List<String> arguments, int expected, String query) {
        if (arguments.size() != expected) {
            throw new IllegalArgumentException(
                    String.format("Query %s requires %d arguments, but got %d",
                            query, expected, arguments.size()));
        }
    }
}
