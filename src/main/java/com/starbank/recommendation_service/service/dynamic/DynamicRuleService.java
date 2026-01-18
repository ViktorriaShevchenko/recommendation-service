package com.starbank.recommendation_service.service.dynamic;

import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;

    public DynamicRuleService(DynamicRuleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DynamicRecommendationRule createRule(DynamicRecommendationRule rule) {
        LocalDateTime now = LocalDateTime.now();
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);
        return repository.save(rule);
    }

    @Transactional
    public DynamicRecommendationRule updateRule(UUID id, DynamicRecommendationRule updatedRule) {
        return repository.findById(id)
                .map(existingRule -> {
                    // Обновляем поля
                    existingRule.setProductName(updatedRule.getProductName());
                    existingRule.setProductText(updatedRule.getProductText());
                    existingRule.setRule(updatedRule.getRule());

                    // Явно обновляем updated_at
                    existingRule.setUpdatedAt(LocalDateTime.now());

                    return repository.save(existingRule);
                })
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
    }
}
