package com.starbank.recommendation_service.repository.dynamic;

import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRecommendationRule, UUID> {

    // 1. Найти правило по product_id (уникальный)
    Optional<DynamicRecommendationRule> findByProductId(UUID productId);

    // 2. Проверить существование по product_id
    boolean existsByProductId(UUID productId);

    // 3. Удалить по product_id (для DELETE /rule/{product_id})
    void deleteByProductId(UUID productId);

    // 4. Найти все правила, отсортированные по дате создания
    List<DynamicRecommendationRule> findAllByOrderByCreatedAtDesc();

    // 5. Найти правила по имени продукта (частичное совпадение)
    List<DynamicRecommendationRule> findByProductNameContainingIgnoreCase(String name);

    // 6. Поиск по содержимому JSONB поля rule
    // Найти правила, которые содержат определенный query в условиях
    @Query(value = "SELECT * FROM dynamic_recommendation_rule drr " +
            "WHERE drr.rule @> :jsonQuery::jsonb",
            nativeQuery = true)
    List<DynamicRecommendationRule> findRulesContainingCondition(@Param("jsonQuery") String jsonQuery);

    // 7. Поиск правил, где есть условие с определенным query
    @Query(value = "SELECT * FROM dynamic_recommendation_rule drr " +
            "WHERE EXISTS (SELECT 1 FROM jsonb_array_elements(drr.rule) r " +
            "WHERE r->>'query' = :queryType)",
            nativeQuery = true)
    List<DynamicRecommendationRule> findRulesByQueryType(@Param("queryType") String queryType);

    long count();
}
