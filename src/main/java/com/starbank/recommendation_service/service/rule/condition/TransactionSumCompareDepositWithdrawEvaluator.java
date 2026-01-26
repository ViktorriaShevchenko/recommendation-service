package com.starbank.recommendation_service.service.rule.condition;

import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionSumCompareDepositWithdrawEvaluator implements ConditionEvaluator {
    @Override
    public boolean supports(String queryType) {
        return "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW".equals(queryType);
    }

    @Override
    public boolean evaluate(RuleCondition condition, UUID userId, RecommendationsRepository repository) {
        validateArgumentsCount(condition.getArguments(), 2, "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");

        var arguments = condition.getArguments();
        int depositSum = repository.transactionSumAndTypeForProductType(
                userId,
                ProductType.valueOf(arguments.get(0)),
                TransactionType.DEPOSIT
        );
        int withdrawSum = repository.transactionSumAndTypeForProductType(
                userId,
                ProductType.valueOf(arguments.get(0)),
                TransactionType.WITHDRAW
        );

        return compareWithOperator(depositSum, arguments.get(1), withdrawSum);
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
