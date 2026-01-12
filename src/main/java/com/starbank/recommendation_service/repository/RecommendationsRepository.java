package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.model.UserRecommendation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
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
        //Реализовать проверку наличия DEBIT продуктов у пользователя
        return false; // Заглушка
    }

    //Пользователь не использует продукты с типом INVEST.
    public boolean hasNoInvestProducts(UUID userId) {
        //Реализовать проверку отсутствия INVEST продуктов у пользователя
        return false; // Заглушка
    }

    //Сумма пополнений продуктов с типом SAVING больше 1000 ₽.
    public boolean hasSavingDepositsOver1000(UUID userId) {
        //Реализовать проверку суммы пополнений SAVING продуктов
        return false; // Заглушка
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
        //Реализовать проверку суммы пополнений DEBIT или SAVING продуктов
        return false; // Заглушка
    }

    //Сумма пополнений по всем продуктам типа DEBIT больше,
    // чем сумма трат по всем продуктам типа DEBIT.
    public boolean hasDebitDepositsGreaterThanExpenses(UUID userId) {
        //Реализовать сравнение суммы пополнений и трат по DEBIT продуктам
        return false; // Заглушка
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
        //Реализовать проверку отсутствия CREDIT продуктов у пользователя
        return false; // Заглушка
    }

    //Сумма трат по всем продуктам типа DEBIT больше, чем 100 000 ₽.
    public boolean hasDebitExpensesOver100000(UUID userId) {
        //Реализовать проверку суммы трат по DEBIT продуктам
        return false; // Заглушка
    }

    //Проверка всех трех правил для "Простой кредит"
    public boolean checkSimpleCreditEligibility(UUID userId) {
        return hasNoCreditProducts(userId)
                && hasDebitDepositsGreaterThanExpenses(userId)
                && hasDebitExpensesOver100000(userId);
    }

    //Получает все рекомендации для пользователя
    public List<UserRecommendation> getRecommendationsForUser(UUID userId) {
        //Реализовать получение всех рекомендаций
        //Метод должен проверять каждую рекомендацию и добавлять её в результат,
        // если пользователь соответствует всем правилам
        return Collections.emptyList(); // Заглушка
    }

}
