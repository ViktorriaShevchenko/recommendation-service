package com.starbank.recommendation_service.service.dynamic;

import com.starbank.recommendation_service.dto.dynamic.DynamicRuleRequest;
import com.starbank.recommendation_service.dto.dynamic.DynamicRuleResponse;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicRuleService {

    private final DynamicRuleRepository repository;

    @Transactional
    public DynamicRuleResponse createRule(DynamicRuleRequest request) {
        if (repository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException("Rule with product_id " + request.getProductId() + " already exists");
        }

        DynamicRecommendationRule rule = DynamicRecommendationRule.builder()
                .productName(request.getProductName())
                .productId(request.getProductId())
                .productText(request.getProductText())
                .rule(request.getRule())
                .build();

        DynamicRecommendationRule savedRule = repository.save(rule);
        return new DynamicRuleResponse(savedRule);
    }

    public List<DynamicRuleResponse> getAllRules() {
        return repository.findAll().stream()
                .map(DynamicRuleResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRuleByProductId(UUID productId) {
        if (!repository.existsByProductId(productId)) {
            throw new IllegalArgumentException("Rule with product_id " + productId + " not found");
        }
        repository.deleteByProductId(productId);
    }
}
