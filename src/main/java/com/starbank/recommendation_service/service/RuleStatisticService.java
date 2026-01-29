package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RuleStatResponseDTO;
import com.starbank.recommendation_service.dto.RuleStatsResponse;
import com.starbank.recommendation_service.entity.dynamic.DynamicRecommendationRule;
import com.starbank.recommendation_service.entity.dynamic.RuleStatistic;
import com.starbank.recommendation_service.repository.dynamic.DynamicRuleRepository;
import com.starbank.recommendation_service.repository.dynamic.RuleStatisticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RuleStatisticService {

    private static final Logger log = LoggerFactory.getLogger(RuleStatisticService.class);

    private final RuleStatisticRepository ruleStatisticRepository;
    private final DynamicRuleRepository dynamicRuleRepository;

    public RuleStatisticService(RuleStatisticRepository ruleStatisticRepository,
                                DynamicRuleRepository dynamicRuleRepository) {
        this.ruleStatisticRepository = ruleStatisticRepository;
        this.dynamicRuleRepository = dynamicRuleRepository;
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

    @Transactional(readOnly = true)
    public RuleStatsResponse getAllRulesStatistics() {
        log.info("Получение статистики для всех динамических правил");

        // Получаем все динамические правила
        List<DynamicRecommendationRule> allRules = dynamicRuleRepository.findAll();

        List<RuleStatResponseDTO> stats = allRules.stream()
                .map(this::getRuleStatisticDTO)
                .collect(Collectors.toList());

        log.info("Найдено {} правил со статистикой", stats.size());
        return new RuleStatsResponse(stats);
    }

    private RuleStatResponseDTO getRuleStatisticDTO(DynamicRecommendationRule rule) {
        try {
            // Ищем статистику для правила
            Optional<RuleStatistic> statisticOpt = ruleStatisticRepository.findByRuleId(rule.getId());

            Long count = statisticOpt
                    .map(RuleStatistic::getCount)
                    .orElse(0L); // Если статистики нет, используем 0

            log.debug("Статистика для правила {}: count = {}", rule.getId(), count);
            return new RuleStatResponseDTO(rule.getId(), count);

        } catch (Exception e) {
            log.error("Ошибка при получении статистики для правила {}: {}", rule.getId(), e.getMessage());
            return new RuleStatResponseDTO(rule.getId(), 0L);
        }
    }
}
