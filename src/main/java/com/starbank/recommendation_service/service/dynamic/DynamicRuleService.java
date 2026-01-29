package com.starbank.recommendation_service.service.dynamic;

import com.starbank.recommendation_service.dto.dynamic.DynamicRuleRequest;
import com.starbank.recommendation_service.dto.dynamic.DynamicRuleResponse;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.service.RuleStatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DynamicRuleService {

    private static final Logger log = LoggerFactory.getLogger(DynamicRuleService.class);
    private final DynamicRuleRepository repository;
    private final RuleStatisticService ruleStatisticService;

    public DynamicRuleService(DynamicRuleRepository repository,
                              RuleStatisticService ruleStatisticService) {
        this.repository = repository;
        this.ruleStatisticService = ruleStatisticService;
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
        try {
            ruleStatisticService.createStatisticForRule(savedRule);
            log.info("Создана запись статистики для нового правила: {}", savedRule.getId());
        } catch (Exception e) {
            log.error("Не удалось создать статистику для правила {}: {}",
                    savedRule.getId(), e.getMessage());
        }
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
