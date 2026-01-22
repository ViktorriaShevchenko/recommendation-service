package com.starbank.recommendation_service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.service.rule.RecommendationRuleSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class FixedRulesTest {

    @Autowired
    private List<RecommendationRuleSet> ruleSets;

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Инициализируем схему и данные вручную
        initSchema();
        initTestData();
    }

    private void initSchema() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR(36) PRIMARY KEY, name VARCHAR(255))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS products (id VARCHAR(36) PRIMARY KEY, type VARCHAR(50) NOT NULL, name VARCHAR(255))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS transactions (id VARCHAR(36) PRIMARY KEY, user_id VARCHAR(36) NOT NULL, product_id VARCHAR(36) NOT NULL, type VARCHAR(50) NOT NULL, amount DECIMAL(15, 2) NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (product_id) REFERENCES products(id))");
    }

    private void initTestData() {
        // Очищаем старые данные
        jdbcTemplate.execute("DELETE FROM transactions");
        jdbcTemplate.execute("DELETE FROM products");
        jdbcTemplate.execute("DELETE FROM users");

        // Пользователь для Invest 500
        UUID investUserId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        jdbcTemplate.execute("INSERT INTO users (id, name) VALUES ('" + investUserId + "', 'Invest 500 User')");
        jdbcTemplate.execute("INSERT INTO products (id, type, name) VALUES ('p1', 'DEBIT', 'Дебетовая карта')");
        jdbcTemplate.execute("INSERT INTO products (id, type, name) VALUES ('p2', 'SAVING', 'Накопительный счет')");
        jdbcTemplate.execute("INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES ('t1', '" + investUserId + "', 'p1', 'DEPOSIT', 50000)");
        jdbcTemplate.execute("INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES ('t2', '" + investUserId + "', 'p2', 'DEPOSIT', 1500)"); // SAVING > 1000

        // Пользователь для Top Saving
        UUID topSavingUserId = UUID.fromString("d4a4d619-9a0c-4fc5-b0cb-76c49409546b");
        jdbcTemplate.execute("INSERT INTO users (id, name) VALUES ('" + topSavingUserId + "', 'Top Saving User')");
        jdbcTemplate.execute("INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES ('t3', '" + topSavingUserId + "', 'p1', 'DEPOSIT', 60000)"); // >= 50000
        jdbcTemplate.execute("INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES ('t4', '" + topSavingUserId + "', 'p1', 'WITHDRAW', 20000)"); // DEPOSIT > WITHDRAW (60000 > 20000)
    }

    @Test
    void testInvest500Rule() {
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

        Optional<RecommendationDTO> result = ruleSets.stream()
                .flatMap(rule -> rule.check(userId).stream())
                .filter(rec -> rec.getName().equals("Invest 500"))
                .findFirst();

        assertThat(result).isPresent();
        assertThat(result.get().getId().toString()).isEqualTo("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    }

    @Test
    void testTopSavingRule() {
        UUID userId = UUID.fromString("d4a4d619-9a0c-4fc5-b0cb-76c49409546b");

        Optional<RecommendationDTO> result = ruleSets.stream()
                .flatMap(rule -> rule.check(userId).stream())
                .filter(rec -> rec.getName().equals("Top Saving"))
                .findFirst();

        assertThat(result).isPresent();
        assertThat(result.get().getId().toString()).isEqualTo("59efc529-2fff-41af-baff-90ccd7402925");
    }

    @Test
    void testNoRulesForUserWithoutTransactions() {
        UUID userId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

        // Создаем пользователя без транзакций
        jdbcTemplate.execute("INSERT INTO users (id, name) VALUES ('" + userId + "', 'No Transactions User')");

        long count = ruleSets.stream()
                .flatMap(rule -> rule.check(userId).stream())
                .count();

        assertThat(count).isEqualTo(0);
    }
}
