package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class IssuedRecommendationDTO {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("recommendation_id")
    private UUID recommendationId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("issued_at")
    private LocalDateTime issuedAt;

    public IssuedRecommendationDTO() {}

    public IssuedRecommendationDTO(UUID userId, UUID recommendationId, LocalDateTime issuedAt) {
        this.userId = userId;
        this.recommendationId = recommendationId;
        this.issuedAt = issuedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(UUID recommendationId) {
        this.recommendationId = recommendationId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
}
