package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.entity.dynamic.RuleStatistic;
import com.starbank.recommendation_service.repository.dynamic.RuleStatisticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RuleStatisticService {

    private static final Logger log = LoggerFactory.getLogger(RuleStatisticService.class);

    private final RuleStatisticRepository ruleStatisticRepository;

    public RuleStatisticService(RuleStatisticRepository ruleStatisticRepository) {
        this.ruleStatisticRepository = ruleStatisticRepository;
    }

    @Transactional
    public void incrementStatistic(UUID ruleId) {
        System.out.println(">>> incrementStatistic called for ruleId: " + ruleId);

        int updated = ruleStatisticRepository.incrementCountByRuleId(ruleId);
        System.out.println(">>> Rows updated: " + updated);
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
