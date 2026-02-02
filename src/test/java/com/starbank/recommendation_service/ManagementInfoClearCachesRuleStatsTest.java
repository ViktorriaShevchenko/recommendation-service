package com.starbank.recommendation_service;

import com.starbank.recommendation_service.controller.ManagementController;
import com.starbank.recommendation_service.controller.RuleStatsController;
import com.starbank.recommendation_service.dto.RuleStatsResponse;
import com.starbank.recommendation_service.service.CacheManagementService;
import com.starbank.recommendation_service.service.RuleStatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ManagementController.class, RuleStatsController.class})
public class ManagementInfoClearCachesRuleStatsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CacheManagementService cacheManagementService;

    @MockBean
    private RuleStatisticService ruleStatisticService;

    @MockBean
    private BuildProperties buildProperties;

    @Test
    void managementInfo_shouldReturnBuildInfo() throws Exception {
        // Arrange
        when(buildProperties.getArtifact()).thenReturn("recommendation-service");
        when(buildProperties.getVersion()).thenReturn("1.0.0");

        // Act & Assert
        mockMvc.perform(get("/management/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("recommendation-service"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void clearCaches_shouldWork() throws Exception {
        // Arrange
        doNothing().when(cacheManagementService).clearAllCaches();

        // Act & Assert
        mockMvc.perform(post("/management/clear-caches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void ruleStats_shouldReturnAllRules() throws Exception {
        // Arrange
        UUID ruleId = UUID.randomUUID();
        RuleStatsResponse response = new RuleStatsResponse(
                List.of(new com.starbank.recommendation_service.dto.RuleStatResponseDTO(ruleId, 0L))
        );
        when(ruleStatisticService.getAllRulesStatistics()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats[0].rule_id").value(ruleId.toString()))
                .andExpect(jsonPath("$.stats[0].count").value(0));
    }
}