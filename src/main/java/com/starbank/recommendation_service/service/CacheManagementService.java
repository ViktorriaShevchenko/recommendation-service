package com.starbank.recommendation_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

@Service
public class CacheManagementService {

    private static final Logger log = LoggerFactory.getLogger(CacheManagementService.class);
    private final RecommendationsRepository recommendationsRepository;

    public CacheManagementService(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    public void clearAllCaches() {
        log.info("Запрос на очистку всех кешей");
        recommendationsRepository.clearAllCaches();
        log.info("Все кеши очищены");
    }
}