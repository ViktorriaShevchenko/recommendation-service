package com.starbank.recommendation_service.service.rule.condition;

import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionSumCompareConditionEvaluator implements ConditionEvaluator {
    @Override
    public boolean supports(String queryType) {
        return "TRANSACTION_SUM_COMPARE".equals(queryType);
    }

    @Override
    public boolean evaluate(RuleCondition condition, UUID userId, RecommendationsRepository repository) {
        validateArgumentsCount(condition.getArguments(), 4, "TRANSACTION_SUM_COMPARE");

        var arguments = condition.getArguments();
        int actualSum = repository.transactionSumAndTypeForProductType(
                userId,
                ProductType.valueOf(arguments.get(0)),
                TransactionType.valueOf(arguments.get(1))
        );
        int requiredValue = Integer.parseInt(arguments.get(3));
        String operator = arguments.get(2);

        return compareWithOperator(actualSum, operator, requiredValue);
    }

    private boolean compareWithOperator(int actual, String operator, int expected) {
        switch (operator) {
            case ">": return actual > expected;
            case "<": return actual < expected;
            case "=": return actual == expected;
            case ">=": return actual >= expected;
            case "<=": return actual <= expected;
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private void validateArgumentsCount(java.util.List<String> arguments, int expected, String query) {
        if (arguments.size() != expected) {
            throw new IllegalArgumentException(
                    String.format("Query %s requires %d arguments, but got %d",
                            query, expected, arguments.size()));
        }
    }
}
