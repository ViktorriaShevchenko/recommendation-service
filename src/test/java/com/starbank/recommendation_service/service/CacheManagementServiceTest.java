package com.starbank.recommendation_service.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheManagementServiceTest {

    @Mock
    private RecommendationsRepository recommendationsRepository;

    @InjectMocks
    private CacheManagementService cacheManagementService;

    @Test
    void clearAllCaches_shouldReturnNumberOfClearedCaches() throws Exception {
        // Arrange
        // Создаем мок кешей через reflection
        Cache<?, ?> mockCache1 = mock(Cache.class);
        Cache<?, ?> mockCache2 = mock(Cache.class);

        // Используем reflection для установки полей
        setPrivateField(recommendationsRepository, "userOfCache", mockCache1);
        setPrivateField(recommendationsRepository, "activeUserOfCache", mockCache2);

        // Act
        int result = cacheManagementService.clearAllCaches();

        // Assert
        assertTrue(result >= 0);
        verify(mockCache1, times(1)).invalidateAll();
        verify(mockCache2, times(1)).invalidateAll();
    }

    @Test
    void clearAllCaches_whenNoCachesFound_shouldReturnZero() {
        // Act
        int result = cacheManagementService.clearAllCaches();

        // Assert
        assertEquals(0, result);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
