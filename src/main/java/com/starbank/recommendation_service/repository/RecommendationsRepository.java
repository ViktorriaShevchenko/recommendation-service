package com.starbank.recommendation_service.repository;

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

    //Методы для "Invest 500"

    //Пользователь использует как минимум один продукт с типом DEBIT.
    public boolean hasDebitProduct(UUID userId) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) > 0 FROM products p
                WHERE p.user_id = ? AND p.type = 'DEBIT'
                """,
                Boolean.class,
                userId.toString());
        return Boolean.TRUE.equals(result);
    }

    //Пользователь не использует продукты с типом INVEST.
    public boolean hasNoInvestProducts(UUID userId) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) = 0 FROM products p
                WHERE p.user_id = ? AND p.type = 'INVEST'
                """,
                Boolean.class,
                userId.toString());
        return Boolean.TRUE.equals(result);
    }

    //Сумма пополнений продуктов с типом SAVING больше 1000 ₽.
    public boolean hasSavingDepositsOver1000(UUID userId) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(t.amount), 0) > 1000
                FROM transactions t JOIN products p
                ON t.product_id = p.id WHERE p.user_id = ?
                AND p.type = 'SAVING' AND t.type = 'DEPOSIT'
                """,
                Boolean.class,
                userId.toString());
        return Boolean.TRUE.equals(result);
    }

    //Проверка всех трех правил для "Invest 500"
    public boolean checkInvest500Eligibility(UUID userId) {
        return hasDebitProduct(userId)
                && hasNoInvestProducts(userId)
                && hasSavingDepositsOver1000(userId);
    }

    //Методы для "Top Saving"

    //Сумма пополнений по всем продуктам типа DEBIT больше или равна 50 000 ₽
    // ИЛИ Сумма пополнений по всем продуктам типа SAVING больше или равна 50 000 ₽.
    public boolean hasDebitOrSavingDepositsOver50000(UUID userId) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(t.amount), 0) >= 50000
                FROM transactions t JOIN products p ON t.product_id = p.id
                WHERE p.user_id = ?
                AND p.type IN ('DEBIT', 'SAVING')
                AND t.type = 'DEPOSIT'
                """,
                Boolean.class,
                userId.toString());
        return Boolean.TRUE.equals(result);
    }

    //Сумма пополнений по всем продуктам типа DEBIT больше,
    // чем сумма трат по всем продуктам типа DEBIT.
    public boolean hasDebitDepositsGreaterThanExpenses(UUID userId) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT
                    COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount ELSE 0 END), 0) >
                    COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE p.user_id = ?
                AND p.type = 'DEBIT'
                """,
                Boolean.class,
                userId.toString());
        return Boolean.TRUE.equals(result);
    }

    //Проверка всех трех правил для "Top Saving"
    public boolean checkTopSavingEligibility(UUID userId) {
        return hasDebitProduct(userId)
                && hasDebitOrSavingDepositsOver50000(userId)
                && hasDebitDepositsGreaterThanExpenses(userId);
    }

    //Методы для "Простой кредит"

    //Пользователь не использует продукты с типом CREDIT.
    public boolean hasNoCreditProducts(UUID userId) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) = 0
                FROM products p
                WHERE p.user_id = ? AND p.type = 'CREDIT'
                """,
                Boolean.class,
                userId.toString());
        return Boolean.TRUE.equals(result);
    }

    //Сумма трат по всем продуктам типа DEBIT больше, чем 100 000 ₽.
    public boolean hasDebitExpensesOver100000(UUID userId) {
        Boolean result = recommendationsJdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(t.amount), 0) > 100000
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE p.user_id = ?
                AND p.type = 'DEBIT'
                AND t.type = 'EXPENSE'
                """,
                Boolean.class,
                userId.toString());
        return Boolean.TRUE.equals(result);
    }

    //Проверка всех трех правил для "Простой кредит"
    public boolean checkSimpleCreditEligibility(UUID userId) {
        return hasNoCreditProducts(userId)
                && hasDebitDepositsGreaterThanExpenses(userId)
                && hasDebitExpensesOver100000(userId);
    }
}
