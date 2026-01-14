package com.starbank.recommendation_service.service.rule;

import com.starbank.recommendation_service.model.UserRecommendation;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<UserRecommendation> check(UUID userId);
}
