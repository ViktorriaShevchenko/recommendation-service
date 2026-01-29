package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RuleStatsResponse {

    @JsonProperty("stats")
    private List<RuleStatResponseDTO> stats;

    public RuleStatsResponse() {
    }

    public RuleStatsResponse(List<RuleStatResponseDTO> stats) {
        this.stats = stats;
    }

    public List<RuleStatResponseDTO> getStats() {
        return stats;
    }

    public void setStats(List<RuleStatResponseDTO> stats) {
        this.stats = stats;
    }
}
