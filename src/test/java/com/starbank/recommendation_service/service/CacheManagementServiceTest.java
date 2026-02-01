package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheManagementServiceTest {

    @Mock
    private RecommendationsRepository recommendationsRepository;

    @InjectMocks
    private CacheManagementService cacheManagementService;

    @Test
    void clearAllCaches_shouldCallRepositoryMethod() {
        // Act
        cacheManagementService.clearAllCaches();

        // Assert
        verify(recommendationsRepository, times(1)).clearAllCaches();
    }

    @Test
    void clearAllCaches_shouldNotThrowException() {
        // Act & Assert - не должно быть исключений
        assertDoesNotThrow(() -> cacheManagementService.clearAllCaches());
    }
}