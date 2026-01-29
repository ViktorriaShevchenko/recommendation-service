package com.starbank.recommendation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dto.RuleStatResponseDTO;
import com.starbank.recommendation_service.dto.RuleStatsResponse;
import com.starbank.recommendation_service.service.RuleStatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(RuleStatsController.class)
public class RuleStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RuleStatisticService ruleStatisticService;

    @Test
    void getAllRulesStatistics_shouldReturnStatsArray() throws Exception {
        // Arrange
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        List<RuleStatResponseDTO> stats = Arrays.asList(
                new RuleStatResponseDTO(ruleId1, 5L),
                new RuleStatResponseDTO(ruleId2, 0L)
        );

        RuleStatsResponse response = new RuleStatsResponse(stats);
        when(ruleStatisticService.getAllRulesStatistics()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats").isArray())
                .andExpect(jsonPath("$.stats", hasSize(2)))
                .andExpect(jsonPath("$.stats[0].rule_id").value(ruleId1.toString()))
                .andExpect(jsonPath("$.stats[0].count").value(5))
                .andExpect(jsonPath("$.stats[1].rule_id").value(ruleId2.toString()))
                .andExpect(jsonPath("$.stats[1].count").value(0));
    }

    @Test
    void getAllRulesStatistics_whenNoRules_shouldReturnEmptyArray() throws Exception {
        // Arrange
        RuleStatsResponse response = new RuleStatsResponse(List.of());
        when(ruleStatisticService.getAllRulesStatistics()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats").isArray())
                .andExpect(jsonPath("$.stats", hasSize(0)));
    }

    @Test
    void getAllRulesStatistics_shouldIncludeRulesWithZeroCount() throws Exception {
        // Arrange
        UUID ruleId = UUID.randomUUID();
        List<RuleStatResponseDTO> stats = List.of(
                new RuleStatResponseDTO(ruleId, 0L)  // Правило с count=0
        );

        RuleStatsResponse response = new RuleStatsResponse(stats);
        when(ruleStatisticService.getAllRulesStatistics()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats[0].count").value(0))
                .andExpect(jsonPath("$.stats[0].rule_id").value(ruleId.toString()));
    }
}
