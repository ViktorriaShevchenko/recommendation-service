package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RecommendationsRepositoryTest {

    @Autowired
    private RecommendationsRepository repository;

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем БД для каждого теста
        clearDatabase();
    }

    private void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM transactions");
        jdbcTemplate.execute("DELETE FROM products");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void hasProduct_UserHasProduct_ReturnsTrue() {
        // Arrange: Вставляем данные для ЭТОГО теста
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

        jdbcTemplate.execute(
                "INSERT INTO users (id, name) VALUES ('" + userId + "', 'Test User')");
        jdbcTemplate.execute(
                "INSERT INTO products (id, type, name) VALUES ('p1', 'DEBIT', 'Дебетовая карта')");
        jdbcTemplate.execute(
                "INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES " +
                        "('t1', '" + userId + "', 'p1', 'DEPOSIT', 50000)");

        // Act
        boolean result = repository.hasProduct(userId, ProductType.DEBIT);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void hasActiveProduct_UserWith5OrMoreTransactions_ReturnsTrue() {
        // Arrange: Вставляем 5+ транзакций для активного пользователя
        UUID userId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        jdbcTemplate.execute(
                "INSERT INTO users (id, name) VALUES ('" + userId + "', 'Active User')");
        jdbcTemplate.execute(
                "INSERT INTO products (id, type, name) VALUES ('p1', 'DEBIT', 'Дебетовая карта')");

        // Вставляем 6 транзакций (>= 5)
        for (int i = 1; i <= 6; i++) {
            jdbcTemplate.execute(
                    String.format(
                            "INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES " +
                                    "('t%d', '%s', 'p1', 'DEPOSIT', %d)",
                            i, userId, 1000 * i
                    )
            );
        }

        // Act
        boolean result = repository.hasActiveProduct(userId, ProductType.DEBIT);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void transactionSumAndTypeForProductType_ValidData_ReturnsCorrectSum() {
        // Arrange
        UUID userId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        jdbcTemplate.execute(
                "INSERT INTO users (id, name) VALUES ('" + userId + "', 'Transaction User')");
        jdbcTemplate.execute(
                "INSERT INTO products (id, type, name) VALUES ('p1', 'DEBIT', 'Дебетовая карта')");

        // Вставляем несколько транзакций для суммирования
        jdbcTemplate.execute(
                "INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES " +
                        "('t1', '" + userId + "', 'p1', 'DEPOSIT', 100000)");
        jdbcTemplate.execute(
                "INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES " +
                        "('t2', '" + userId + "', 'p1', 'DEPOSIT', 50000)");
        jdbcTemplate.execute(
                "INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES " +
                        "('t3', '" + userId + "', 'p1', 'WITHDRAW', 30000)");

        // Act: Сумма DEPOSIT транзакций = 100000 + 50000 = 150000
        int result = repository.transactionSumAndTypeForProductType(
                userId, ProductType.DEBIT, TransactionType.DEPOSIT);

        // Assert
        assertThat(result).isEqualTo(150000);
    }

    @Test
    void hasActiveProduct_UserWithLessThan5Transactions_ReturnsFalse() {
        // Arrange: Вставляем только 3 транзакции (< 5)
        UUID userId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

        jdbcTemplate.execute(
                "INSERT INTO users (id, name) VALUES ('" + userId + "', 'Inactive User')");
        jdbcTemplate.execute(
                "INSERT INTO products (id, type, name) VALUES ('p1', 'DEBIT', 'Дебетовая карта')");

        for (int i = 1; i <= 3; i++) {
            jdbcTemplate.execute(
                    String.format(
                            "INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES " +
                                    "('t%d', '%s', 'p1', 'DEPOSIT', %d)",
                            i, userId, 1000 * i
                    )
            );
        }

        // Act
        boolean result = repository.hasActiveProduct(userId, ProductType.DEBIT);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void hasProduct_UserHasNoProduct_ReturnsFalse() {
        // Arrange: Создаем пользователя без транзакций
        UUID userId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

        jdbcTemplate.execute(
                "INSERT INTO users (id, name) VALUES ('" + userId + "', 'No Product User')");
        jdbcTemplate.execute(
                "INSERT INTO products (id, type, name) VALUES ('p1', 'DEBIT', 'Дебетовая карта')");
        // НЕ вставляем транзакции!

        // Act
        boolean result = repository.hasProduct(userId, ProductType.DEBIT);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void transactionSumAndTypeForProductType_NoTransactions_ReturnsZero() {
        // Arrange
        UUID userId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

        jdbcTemplate.execute(
                "INSERT INTO users (id, name) VALUES ('" + userId + "', 'No Transactions User')");
        jdbcTemplate.execute(
                "INSERT INTO products (id, type, name) VALUES ('p1', 'DEBIT', 'Дебетовая карта')");
        // НЕ вставляем транзакции!

        // Act
        int result = repository.transactionSumAndTypeForProductType(
                userId, ProductType.DEBIT, TransactionType.DEPOSIT);

        // Assert
        assertThat(result).isEqualTo(0);
    }
}
