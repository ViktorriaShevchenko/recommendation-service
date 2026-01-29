package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.RuleStatsResponse;
import com.starbank.recommendation_service.service.RuleStatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private static final Logger log = LoggerFactory.getLogger(RuleStatsController.class);

    private final RuleStatisticService ruleStatisticService;

    public RuleStatsController(RuleStatisticService ruleStatisticService) {
        this.ruleStatisticService = ruleStatisticService;
    }

    @GetMapping("/stats")
    public ResponseEntity<RuleStatsResponse> getAllRulesStatistics() {
        try {
            log.info("Запрос на получение статистики всех правил");
            RuleStatsResponse response = ruleStatisticService.getAllRulesStatistics();
            log.info("Статистика успешно получена, количество записей: {}",
                    response.getStats() != null ? response.getStats().size() : 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при получении статистики правил: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
