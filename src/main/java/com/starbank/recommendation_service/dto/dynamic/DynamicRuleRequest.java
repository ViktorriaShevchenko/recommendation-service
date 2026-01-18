package com.starbank.recommendation_service.dto.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starbank.recommendation_service.entity.dynamic.RuleCondition;

import java.util.List;
import java.util.UUID;

public class DynamicRuleRequest {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_text")
    private String productText;

    @JsonProperty("rule")
    private List<RuleCondition> rule;

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

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public void setProductText(String productText) {
        this.productText = productText;
    }
    public void setRule(List<RuleCondition> rule) {
        this.rule = rule;
    }
}
