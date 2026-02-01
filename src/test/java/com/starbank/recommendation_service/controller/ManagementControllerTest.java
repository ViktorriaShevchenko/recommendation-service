package com.starbank.recommendation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.service.CacheManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ManagementController.class)
public class ManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CacheManagementService cacheManagementService;

    @MockBean
    private BuildProperties buildProperties;

    @Test
    void clearCaches_shouldReturnSuccessResponse() throws Exception {
        // Arrange
        doNothing().when(cacheManagementService).clearAllCaches();

        // Act & Assert
        mockMvc.perform(post("/management/clear-caches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All caches cleared successfully"))
                .andExpect(jsonPath("$.caches_cleared").doesNotExist()); // Этого поля больше нет
    }

    @Test
    void clearCaches_whenServiceThrowsException_shouldReturnErrorResponse() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Cache error"))
                .when(cacheManagementService).clearAllCaches();

        // Act & Assert
        mockMvc.perform(post("/management/clear-caches"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Failed to clear")))
                .andExpect(jsonPath("$.caches_cleared").doesNotExist()); // Этого поля больше нет
    }

    @Test
    void getBuildInfo_shouldReturnNameAndVersion() throws Exception {
        // Arrange
        when(buildProperties.getArtifact()).thenReturn("recommendation-service");
        when(buildProperties.getVersion()).thenReturn("1.0.0");

        // Act & Assert
        mockMvc.perform(get("/management/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("recommendation-service"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }
}