package com.starbank.recommendation_service;

import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"/schema-test.sql", "/test-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RecommendationsRepositoryTest {

    @Autowired
    private RecommendationsRepository repository;

    @Test
    void shouldReturnTrueForUserWithDebitProduct() {
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        assertThat(repository.hasProduct(userId, ProductType.DEBIT)).isTrue();
    }

    @Test
    void shouldReturnFalseForUserWithoutDebitProduct() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        assertThat(repository.hasProduct(userId, ProductType.DEBIT)).isFalse();
    }
}
