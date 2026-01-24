package com.starbank.recommendation_service.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate recommendationsJdbcTemplate;

    private final Cache<String, Boolean> userOfCache;
    private final Cache<String, Boolean> activeUserOfCache;
    private final Cache<String, Integer> transactionSumCache;

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate recommendationsJdbcTemplate) {
        this.recommendationsJdbcTemplate = recommendationsJdbcTemplate;

        this.userOfCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        this.activeUserOfCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        this.transactionSumCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public boolean hasProduct(UUID userId, ProductType productType) {
        String key = userId + ":" + productType.name();
        return userOfCache.get(key, k -> executeHasProduct(userId, productType));
    }

    private boolean executeHasProduct(UUID userId, ProductType productType) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
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
        String key = userId + ":" + productType.name() + ":" + transactionType.name();
        return transactionSumCache.get(key, k ->
                executeTransactionSum(userId, productType, transactionType));
    }

    private int executeTransactionSum(UUID userId, ProductType productType, TransactionType transactionType) {
        Integer result = recommendationsJdbcTemplate.queryForObject(
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
        return result != null ? result : 0;
    }
    public boolean hasActiveProduct(UUID userId, ProductType productType) {
        String key = userId + ":" + productType.name() + ":active";
        return activeUserOfCache.get(key, k -> executeHasActiveProduct(userId, productType));
    }

    private boolean executeHasActiveProduct(UUID userId, ProductType productType) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) >= 5
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
                """,
                Boolean.class,
                userId.toString(),
                productType.name());
        return Boolean.TRUE.equals(result);
    }
}
