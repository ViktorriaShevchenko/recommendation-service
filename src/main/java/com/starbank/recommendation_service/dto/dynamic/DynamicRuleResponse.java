package com.starbank.recommendation_service.dto.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;

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

    public DynamicRuleResponse() {
    }

    public DynamicRuleResponse(DynamicRecommendationRule entity) {
        this.id = entity.getId();
        this.productName = entity.getProductName();
        this.productId = entity.getProductId();
        this.productText = entity.getProductText();
        this.rule = entity.getRule();
    }

    public DynamicRuleResponse(UUID id, String productName, UUID productId, String productText, List<RuleCondition> rule) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<RuleCondition> getRule() {
        return rule;
    }

    public void setRule(List<RuleCondition> rule) {
        this.rule = rule;
    }
}
