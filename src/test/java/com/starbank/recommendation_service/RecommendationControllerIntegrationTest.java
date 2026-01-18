package com.starbank.recommendation_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/schema-test.sql", "/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RecommendationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // === Тестовые пользователи из ТЗ ===
    private static final String INVEST_500_USER_ID = "cd515076-5d8a-44be-930e-8d4fcb79f42d";
    private static final String TOP_SAVING_USER_ID = "d4a4d619-9a0c-4fc5-b0cb-76c49409546b";
    private static final String SIMPLE_CREDIT_USER_ID = "1f9b149c-6577-448a-bc94-16bea229b71a";

    // === ТЕСТ 1: Для каждого тестового пользователя из ТЗ ===

    @Test
    void shouldReturnInvest500RecommendationForTestUser() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", INVEST_500_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(INVEST_500_USER_ID))
                .andExpect(jsonPath("$.recommendations[0].id").value("147f6a0f-3b91-413b-ab99-87f081d60d5a"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"))
                .andExpect(jsonPath("$.recommendations[0].text").isNotEmpty());
    }

    @Test
    void shouldReturnTopSavingRecommendationForTestUser() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", TOP_SAVING_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(TOP_SAVING_USER_ID))
                .andExpect(jsonPath("$.recommendations[0].id").value("59efc529-2fff-41af-baff-90ccd7402925"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Top Saving"))
                .andExpect(jsonPath("$.recommendations[0].text").isNotEmpty());
    }

    @Test
    void shouldReturnSimpleCreditRecommendationForTestUser() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", SIMPLE_CREDIT_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(SIMPLE_CREDIT_USER_ID))
                .andExpect(jsonPath("$.recommendations[0].id").value("59efc529-2fff-41af-baff-90ccd7402925"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Top Saving"))
                .andExpect(jsonPath("$.recommendations[0].text").isNotEmpty());
    }

    // === ТЕСТ 2: Для несуществующего пользователя ===

    @Test
    void shouldReturnEmptyArrayForNonExistentUser() throws Exception {
        UUID nonExistentUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        mockMvc.perform(get("/recommendation/{userId}", nonExistentUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(nonExistentUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }

    @Test
    void shouldReturnEmptyArrayForRandomUser() throws Exception {
        UUID randomUserId = UUID.randomUUID();

        mockMvc.perform(get("/recommendation/{userId}", randomUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(randomUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }

    // === ТЕСТ 3: Для некорректного UUID ===

    @Test
    void shouldReturn400ForInvalidUUIDFormat() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForUUIDWithExtraCharacters() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", INVEST_500_USER_ID + "-extra"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForMalformedUUID() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", "123-456-789"))
                .andExpect(status().isBadRequest());
    }

    // === ТЕСТ 4: Проверка структуры JSON-ответа ===

    @Test
    void shouldReturnCorrectJsonStructure() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", INVEST_500_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                // Проверяем обязательные поля согласно ТЗ
                .andExpect(jsonPath("$.user_id").exists())
                .andExpect(jsonPath("$.recommendations").exists())
                .andExpect(jsonPath("$.recommendations").isArray())
                // Проверяем структуру рекомендации
                .andExpect(jsonPath("$.recommendations[0].id").exists())
                .andExpect(jsonPath("$.recommendations[0].name").exists())
                .andExpect(jsonPath("$.recommendations[0].text").exists());
    }

    @Test
    void shouldHaveCorrectContentTypeHeader() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", INVEST_500_USER_ID))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));
    }
}
