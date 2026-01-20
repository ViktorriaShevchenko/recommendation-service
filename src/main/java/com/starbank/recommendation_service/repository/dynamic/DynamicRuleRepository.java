package com.starbank.recommendation_service.repository.dynamic;

import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRecommendationRule, UUID> {

    // Для DELETE /rule/{product_id}
    void deleteByProductId(UUID productId);

    // Для проверки существования при создании
    boolean existsByProductId(UUID productId);

    // Для GET /rule (сортировка по дате создания)
    List<DynamicRecommendationRule> findAllByOrderByCreatedAtDesc();

    // Опционально: найти по product_id
    Optional<DynamicRecommendationRule> findByProductId(UUID productId);
}
