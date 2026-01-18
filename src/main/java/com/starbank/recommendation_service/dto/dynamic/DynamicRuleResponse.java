package com.starbank.recommendation_service.dto.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DynamicRuleResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_text")
    private String productText;

    @JsonProperty("rule")
    private List<RuleCondition> rule;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public DynamicRuleResponse(DynamicRecommendationRule entity) {
        this.id = entity.getId();
        this.productName = entity.getProductName();
        this.productId = entity.getProductId();
        this.productText = entity.getProductText();
        this.rule = entity.getRule();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    public UUID getId() {
        return id;
    }
    public String getProductName() {
        return productName;
    }
    public UUID getProductId() {
        return productId;
    }
    public String getProductText() {
        return productText;
    }
    public List<RuleCondition> getRule() {
        return rule;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
