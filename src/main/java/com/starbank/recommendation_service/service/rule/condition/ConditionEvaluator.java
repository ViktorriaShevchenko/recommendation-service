package com.starbank.recommendation_service.service.rule.condition;

import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.repository.RecommendationsRepository;

import java.util.UUID;

public interface ConditionEvaluator {
    boolean supports(String queryType);
    boolean evaluate(RuleCondition condition, UUID userId, RecommendationsRepository repository);
}
