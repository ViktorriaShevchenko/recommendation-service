package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.entity.dynamic.RuleStatistic;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.repository.dynamic.RuleStatisticRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RuleStatisticService {

    private final RuleStatisticRepository ruleStatisticRepository;
    private final DynamicRuleRepository dynamicRuleRepository; // ← добавляем

    public RuleStatisticService(RuleStatisticRepository ruleStatisticRepository,
                                DynamicRuleRepository dynamicRuleRepository) { // ← добавляем
        this.ruleStatisticRepository = ruleStatisticRepository;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    @Transactional
    public void incrementStatistic(UUID ruleId) {
        int updated = ruleStatisticRepository.incrementCountByRuleId(ruleId);

        if (updated == 0) {
            DynamicRecommendationRule rule = dynamicRuleRepository.findById(ruleId)
                    .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + ruleId));

            RuleStatistic statistic = new RuleStatistic(rule, 1L);
            statistic.setLastTriggeredAt(LocalDateTime.now());
            ruleStatisticRepository.save(statistic);
        }
    }

    @Transactional(readOnly = true)
    public RuleStatistic getStatisticByRuleId(UUID ruleId) {
        return ruleStatisticRepository.findByRuleId(ruleId)
                .orElse(null);
    }

    @Transactional
    public void deleteStatisticByRuleId(UUID ruleId) {
        ruleStatisticRepository.deleteByRuleId(ruleId);
    }
}
