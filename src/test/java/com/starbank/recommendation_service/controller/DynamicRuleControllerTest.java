package com.starbank.recommendation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dto.dynamic.DynamicRuleRequest;
import com.starbank.recommendation_service.dto.dynamic.DynamicRuleResponse;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.service.dynamic.DynamicRuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DynamicRuleController.class)
public class DynamicRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DynamicRuleService dynamicRuleService;

    @Test
    void createRule_ValidRequest_ReturnsCreatedRule() throws Exception {
        // Arrange
        DynamicRuleRequest request = new DynamicRuleRequest();
        request.setProductName("Test Product");
        request.setProductId(UUID.randomUUID());
        request.setProductText("Test Text");
        request.setRule(Arrays.asList(
                new RuleCondition("USER_OF", Arrays.asList("DEBIT"), false)
        ));

        DynamicRuleResponse response = new DynamicRuleResponse();
        response.setId(UUID.randomUUID());
        response.setProductName("Test Product");
        response.setProductId(request.getProductId());
        response.setProductText("Test Text");
        response.setRule(request.getRule());

        when(dynamicRuleService.createRule(any(DynamicRuleRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value("Test Product"))
                .andExpect(jsonPath("$.product_id").value(request.getProductId().toString()));
    }

    @Test
    void getAllRules_ReturnsList() throws Exception {
        // Arrange
        DynamicRuleResponse rule1 = new DynamicRuleResponse();
        rule1.setId(UUID.randomUUID());
        rule1.setProductName("Rule 1");

        DynamicRuleResponse rule2 = new DynamicRuleResponse();
        rule2.setId(UUID.randomUUID());
        rule2.setProductName("Rule 2");

        List<DynamicRuleResponse> rules = Arrays.asList(rule1, rule2);
        when(dynamicRuleService.getAllRules()).thenReturn(rules);

        // Act & Assert
        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteRule_ValidId_ReturnsNoContent() throws Exception {
        // Arrange
        UUID ruleId = UUID.randomUUID();
        doNothing().when(dynamicRuleService).deleteRuleByProductId(ruleId);

        // Act & Assert
        mockMvc.perform(delete("/rule/{id}", ruleId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRule_NonExistentId_ReturnsNotFound() throws Exception {
        // Arrange
        UUID ruleId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Rule not found"))
                .when(dynamicRuleService).deleteRuleByProductId(ruleId);

        // Act & Assert
        mockMvc.perform(delete("/rule/{id}", ruleId))
                .andExpect(status().isNotFound());
    }
}
