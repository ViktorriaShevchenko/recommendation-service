package com.starbank.recommendation_service;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RecommendationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void shouldReturnInvest500RecommendationForTestUser() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        List<RecommendationDTO> recommendations = List.of(
                new RecommendationDTO(
                        UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                        "Invest 500",
                        "Текст рекомендации..."
                )
        );
        RecommendationResponse response = new RecommendationResponse(userId, recommendations);

        when(recommendationService.getRecommendationsForUser(userId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations[0].id").value("147f6a0f-3b91-413b-ab99-87f081d60d5a"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"));
    }

    @Test
    void shouldReturnEmptyArrayForNonExistentUser() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        RecommendationResponse response = new RecommendationResponse(userId, List.of());

        when(recommendationService.getRecommendationsForUser(userId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }
}
