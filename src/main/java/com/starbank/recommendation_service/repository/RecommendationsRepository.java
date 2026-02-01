package com.starbank.recommendation_service.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationsRepository {

    private static final Logger log = LoggerFactory.getLogger(RecommendationsRepository.class);

    private final JdbcTemplate recommendationsJdbcTemplate;

    private final Cache<String, Boolean> userOfCache;
    private final Cache<String, Boolean> activeUserOfCache;
    private final Cache<String, Integer> transactionSumCache;
    private final Cache<String, UUID> userIdCache;
    private final Cache<String, String> userFullNameCache;

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

        this.userIdCache = Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();

        this.userFullNameCache = Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    public void clearAllCaches() {
        userOfCache.invalidateAll();
        activeUserOfCache.invalidateAll();
        transactionSumCache.invalidateAll();
        userIdCache.invalidateAll();
        userFullNameCache.invalidateAll();
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

    public Optional<UUID> findSingleUserIdByUsername(String username) {
        String key = "username:" + username.toLowerCase();
        return Optional.ofNullable(
                userIdCache.get(key, k -> executeFindSingleUserIdByUsername(username))
        );
    }

    private UUID executeFindSingleUserIdByUsername(String username) {
        try {
            username = username.trim().toLowerCase();

            String sql = "SELECT id FROM users WHERE LOWER(username) = ?";

            List<UUID> results = recommendationsJdbcTemplate.query(
                    sql,
                    (rs, rowNum) -> UUID.fromString(rs.getString("id")),
                    username
            );

            if (results.size() == 1) {
                return results.get(0);
            } else {
                return null;
            }

        } catch (Exception e) {
            log.error("Error finding user by username: {}", username, e);
            return null;
        }
    }

    public String getUserFullName(UUID userId) {
        String key = "fullname:" + userId.toString();
        return userFullNameCache.get(key, k -> executeGetUserFullName(userId));
    }

    private String executeGetUserFullName(UUID userId) {
        try {
            log.debug("Getting full name for user ID: {}", userId);

            String sql = "SELECT first_name, last_name FROM users WHERE id = ?";

            return recommendationsJdbcTemplate.query(sql, rs -> {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");

                    if (firstName != null && lastName != null) {
                        return firstName + " " + lastName;
                    } else if (firstName != null) {
                        return firstName;
                    } else {
                        return lastName;
                    }
                }
                return null;
            }, userId.toString());

        } catch (Exception e) {
            log.error("Error getting user full name for ID: {}", userId, e);
            return null;
        }
    }
}
