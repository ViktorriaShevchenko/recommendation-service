package com.starbank.recommendation_service.service.rule.condition;

import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConditionEvaluatorService {

    private final List<ConditionEvaluator> evaluators;

    public ConditionEvaluatorService(List<ConditionEvaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public boolean evaluateCondition(RuleCondition condition, UUID userId, RecommendationsRepository repository) {
        ConditionEvaluator evaluator = findEvaluator(condition.getQuery());
        if (evaluator == null) {
            throw new IllegalArgumentException("Unknown query type: " + condition.getQuery());
        }

        try {
            boolean result = evaluator.evaluate(condition, userId, repository);
            return condition.isNegate() ? !result : result;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private ConditionEvaluator findEvaluator(String queryType) {
        return evaluators.stream()
                .filter(evaluator -> evaluator.supports(queryType))
                .findFirst()
                .orElse(null);
    }
}
