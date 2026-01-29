package com.starbank.recommendation_service.service;

import com.github.benmanes.caffeine.cache.Cache;
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

    public int clearAllCaches() {
        log.info("Запрос на очистку всех кешей");

        int clearedCount = 0;

        try {
            // Очищаем кеши через reflection, так как они приватные в RecommendationsRepository
            clearedCount = clearCachesByReflection();

            log.info("Очищено {} кешей", clearedCount);
            return clearedCount;

        } catch (Exception e) {
            log.error("Ошибка при очистке кешей: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear caches: " + e.getMessage(), e);
        }
    }

    private int clearCachesByReflection() throws Exception {
        int cleared = 0;

        // Получаем приватные поля кешей через reflection
        var fields = RecommendationsRepository.class.getDeclaredFields();

        for (var field : fields) {
            if (field.getType().getName().contains("Cache")) {
                field.setAccessible(true);
                Cache<?, ?> cache = (Cache<?, ?>) field.get(recommendationsRepository);
                if (cache != null) {
                    cache.invalidateAll();
                    log.debug("Очищен кеш: {}", field.getName());
                    cleared++;
                }
            }
        }

        return cleared;
    }
}
