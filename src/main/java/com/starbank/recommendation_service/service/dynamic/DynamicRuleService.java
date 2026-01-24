package com.starbank.recommendation_service.service.dynamic;

import com.starbank.recommendation_service.dto.dynamic.DynamicRuleRequest;
import com.starbank.recommendation_service.dto.dynamic.DynamicRuleResponse;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;

    public DynamicRuleService(DynamicRuleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DynamicRuleResponse createRule(DynamicRuleRequest request) {
        if (repository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException("Rule with product_id " + request.getProductId() + " already exists");
        }

        DynamicRecommendationRule rule = new DynamicRecommendationRule();
        rule.setProductName(request.getProductName());
        rule.setProductId(request.getProductId());
        rule.setProductText(request.getProductText());
        rule.setRule(request.getRule());

        DynamicRecommendationRule savedRule = repository.save(rule);
        return new DynamicRuleResponse(savedRule);
    }

    public List<DynamicRuleResponse> getAllRules() {
        return repository.findAll().stream()
                .map(DynamicRuleResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRuleById(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Rule with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deleteRuleByProductId(UUID productId) {
        repository.findByProductId(productId)
                .ifPresent(rule -> repository.deleteById(rule.getId()));
    }
}
