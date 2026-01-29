package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.CacheClearResponse;
import com.starbank.recommendation_service.service.CacheManagementService;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private static final Logger log = LoggerFactory.getLogger(ManagementController.class);

    private final CacheManagementService cacheManagementService;

    public ManagementController(CacheManagementService cacheManagementService) {
        this.cacheManagementService = cacheManagementService;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<CacheClearResponse> clearCaches() {
        try {
            log.info("Получен запрос на очистку кешей");

            int clearedCount = cacheManagementService.clearAllCaches();

            CacheClearResponse response = new CacheClearResponse(
                    true,
                    "All caches cleared successfully",
                    clearedCount
            );

            log.info("Кеши очищены. Очищено: {}", clearedCount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка при очистке кешей: {}", e.getMessage(), e);

            CacheClearResponse response = new CacheClearResponse(
                    false,
                    "Failed to clear caches: " + e.getMessage(),
                    0
            );

            return ResponseEntity.internalServerError().body(response);
        }
    }
}
