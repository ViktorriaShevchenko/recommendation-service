package com.starbank.recommendation_service.atomicity;

import com.starbank.recommendation_service.repository.dynamic.RuleStatisticRepository;
import com.starbank.recommendation_service.service.RuleStatisticService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleStatisticServiceConcurrencyTest {

    @Mock
    private RuleStatisticRepository ruleStatisticRepository;

    @Test
    void incrementStatistic_shouldHandleConcurrentIncrementsAtomically() throws InterruptedException {
        // Arrange
        UUID ruleId = UUID.randomUUID();
        int threadCount = 10;
        int incrementsPerThread = 100;

        // Используем CountDownLatch для одновременного старта
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // Мокаем репозиторий - каждый вызов увеличивает счетчик на 1
        AtomicInteger totalIncrementCalls = new AtomicInteger(0);
        when(ruleStatisticRepository.incrementCountByRuleId(eq(ruleId)))
                .thenAnswer(invocation -> {
                    totalIncrementCalls.incrementAndGet();
                    return 1; // Симулируем успешное обновление
                });

        RuleStatisticService service = new RuleStatisticService(ruleStatisticRepository, null);

        // Создаем ExecutorService для многопоточного тестирования
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // Act: Запускаем потоки
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Ждем сигнала старта

                    for (int j = 0; j < incrementsPerThread; j++) {
                        service.incrementStatistic(ruleId);
                    }

                    endLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Запускаем все потоки одновременно
        startLatch.countDown();

        // Ждем завершения всех потоков (максимум 30 секунд)
        boolean completed = endLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // Assert
        assert completed : "Тест не завершился за отведенное время";

        // Проверяем, что метод вызывался правильное количество раз
        int expectedTotalCalls = threadCount * incrementsPerThread;
        assertEquals(expectedTotalCalls, totalIncrementCalls.get(),
                "Должно быть " + expectedTotalCalls + " вызовов incrementCountByRuleId");

        // Проверяем, что метод вызывался с правильным ruleId
        verify(ruleStatisticRepository, times(expectedTotalCalls)).incrementCountByRuleId(ruleId);
    }

    @Test
    void incrementStatistic_whenMultipleRules_shouldNotInterfere() throws InterruptedException {
        // Arrange
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount * 2);

        AtomicInteger rule1Calls = new AtomicInteger(0);
        AtomicInteger rule2Calls = new AtomicInteger(0);

        when(ruleStatisticRepository.incrementCountByRuleId(eq(ruleId1)))
                .thenAnswer(invocation -> {
                    rule1Calls.incrementAndGet();
                    return 1;
                });

        when(ruleStatisticRepository.incrementCountByRuleId(eq(ruleId2)))
                .thenAnswer(invocation -> {
                    rule2Calls.incrementAndGet();
                    return 1;
                });

        RuleStatisticService service = new RuleStatisticService(ruleStatisticRepository, null);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // Act: Потоки увеличивают оба счетчика
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                service.incrementStatistic(ruleId1);
                service.incrementStatistic(ruleId2);
                latch.countDown();
                latch.countDown();
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // Assert
        assertEquals(threadCount, rule1Calls.get(),
                "Rule 1 должен быть увеличен " + threadCount + " раз");
        assertEquals(threadCount, rule2Calls.get(),
                "Rule 2 должен быть увеличен " + threadCount + " раз");

        verify(ruleStatisticRepository, times(threadCount)).incrementCountByRuleId(ruleId1);
        verify(ruleStatisticRepository, times(threadCount)).incrementCountByRuleId(ruleId2);
    }
}
