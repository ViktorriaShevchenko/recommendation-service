package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.BuildInfoResponse;
import com.starbank.recommendation_service.dto.CacheClearResponse;
import com.starbank.recommendation_service.service.CacheManagementService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private static final Logger log = LoggerFactory.getLogger(ManagementController.class);
    private final CacheManagementService cacheManagementService;
    private final BuildProperties buildProperties;

    public ManagementController(CacheManagementService cacheManagementService,
                                BuildProperties buildProperties) {
        this.cacheManagementService = cacheManagementService;
        this.buildProperties = buildProperties;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<CacheClearResponse> clearCaches() {
        try {
            log.info("Получен POST-запрос на очистку кешей");

            cacheManagementService.clearAllCaches();

            CacheClearResponse response = new CacheClearResponse(
                    true,
                    "All caches cleared successfully"
            );

            log.info("Кеши успешно очищены");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка при очистке кешей: {}", e.getMessage(), e);

            CacheClearResponse response = new CacheClearResponse(
                    false,
                    "Failed to clear caches: " + e.getMessage()
            );

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<BuildInfoResponse> getBuildInfo() {
        try {
            log.info("Запрос на получение информации о сборке");

            BuildInfoResponse response = new BuildInfoResponse();
            response.setName(buildProperties.getArtifact());
            response.setVersion(buildProperties.getVersion());

            log.info("Build info: name={}, version={}",
                    response.getName(), response.getVersion());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка при получении информации о сборке: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}