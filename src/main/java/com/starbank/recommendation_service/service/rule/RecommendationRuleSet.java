package com.starbank.recommendation_service.service.rule;

import com.starbank.recommendation_service.dto.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<RecommendationDTO> check(UUID userId);
}
