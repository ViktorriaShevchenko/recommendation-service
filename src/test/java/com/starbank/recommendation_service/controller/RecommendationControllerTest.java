package com.starbank.recommendation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void shouldReturnRecommendationsForValidUser() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

        List<RecommendationDTO> recommendations = Arrays.asList(
                new RecommendationDTO(
                        UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                        "Invest 500",
                        "Текст рекомендации..."
                )
        );

        RecommendationResponse mockResponse = new RecommendationResponse(userId, recommendations);

        when(recommendationService.getRecommendationsForUser(any(UUID.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations[0].id").value("147f6a0f-3b91-413b-ab99-87f081d60d5a"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"))
                .andExpect(jsonPath("$.recommendations[0].text").value("Текст рекомендации..."));
    }

    @Test
    void shouldReturnEmptyRecommendations() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        RecommendationResponse mockResponse = new RecommendationResponse(userId, List.of());

        when(recommendationService.getRecommendationsForUser(any(UUID.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }

    @Test
    void shouldReturn400ForInvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}
