package com.starbank.recommendation_service.repository.dynamic;

import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRecommendationRule, UUID> {

    void deleteByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    Optional<DynamicRecommendationRule> findByProductId(UUID productId);
}
