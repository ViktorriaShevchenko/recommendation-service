package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.entity.dynamic.RuleStatistic;
import com.starbank.recommendation_service.repository.dynamic.RuleStatisticRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RuleStatisticService {

    private final RuleStatisticRepository ruleStatisticRepository;

    public RuleStatisticService(RuleStatisticRepository ruleStatisticRepository) {
        this.ruleStatisticRepository = ruleStatisticRepository;
    }

    @Transactional
    public void incrementStatistic(UUID ruleId) {
        int updated = ruleStatisticRepository.incrementCountByRuleId(ruleId);

        // Если записи нет - создаем
        if (updated == 0) {
            // Здесь нужно получить правило из базы
        }
    }

    @Transactional
    public void createStatisticForRule(DynamicRecommendationRule rule) {
        if (!ruleStatisticRepository.existsByRuleId(rule.getId())) {
            RuleStatistic statistic = new RuleStatistic(rule);
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
