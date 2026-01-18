package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate recommendationsJdbcTemplate;

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate recommendationsJdbcTemplate) {
        this.recommendationsJdbcTemplate = recommendationsJdbcTemplate;
    }

    public boolean hasProduct(UUID userId, ProductType productType) {
        boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) > 0
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
                """,
                Boolean.class,
                userId.toString(),
                productType.name());
        return Boolean.TRUE.equals(result);
    }

    public int transactionSumAndTypeForProductType(
            UUID userId, ProductType productType, TransactionType transactionType) {
        return recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(t.amount), 0)
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ?
                AND p.type = ?
                AND t.type = ?
                """,
                Integer.class,
                userId.toString(),
                productType.name(),
                transactionType.name());
    }
}
