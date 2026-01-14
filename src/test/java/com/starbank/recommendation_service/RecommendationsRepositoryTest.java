package com.starbank.recommendation_service;

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
        assertThat(repository.hasDebitProduct(userId)).isTrue();
    }

    @Test
    void shouldReturnFalseForUserWithoutDebitProduct() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        assertThat(repository.hasDebitProduct(userId)).isFalse();
    }

    @Test
    void shouldCheckInvest500Eligibility() {
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        assertThat(repository.checkInvest500Eligibility(userId)).isTrue();
    }

    @Test
    void shouldCheckTopSavingEligibility() {
        UUID userId = UUID.fromString("d4a4d619-9a0c-4fc5-b0cb-76c49409546b");
        assertThat(repository.checkTopSavingEligibility(userId)).isTrue();
    }

    @Test
    void shouldCheckSimpleCreditEligibility() {
        UUID userId = UUID.fromString("1f9b149c-6577-448a-bc94-16bea229b71a");
        assertThat(repository.checkSimpleCreditEligibility(userId)).isTrue();
    }
}
