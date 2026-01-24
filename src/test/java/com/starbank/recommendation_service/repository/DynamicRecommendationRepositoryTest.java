package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.dto.IssuedRecommendationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DynamicRecommendationRepositoryTest {
    @Autowired
    private DynamicRecommendationRepository dynamicRecommendationRepository;

    @Test
    void save_NewRecommendation_SavesSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID recommendationId = UUID.randomUUID();
        String productName = "Test Product";
        String productText = "Test Text";

        // Act
        dynamicRecommendationRepository.save(userId, recommendationId, productName, productText);

        // Assert
        assertThat(dynamicRecommendationRepository.isAlreadyIssued(userId, recommendationId)).isTrue();
    }

    @Test
    void save_DuplicateRecommendation_DoesNotThrowException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID recommendationId = UUID.randomUUID();
        String productName = "Test Product";
        String productText = "Test Text";

        // Act - Save twice
        dynamicRecommendationRepository.save(userId, recommendationId, productName, productText);
        dynamicRecommendationRepository.save(userId, recommendationId, productName, productText);

        // Assert - No exception, duplicate ignored
        assertThat(dynamicRecommendationRepository.isAlreadyIssued(userId, recommendationId)).isTrue();
    }

    @Test
    void isAlreadyIssued_NotIssued_ReturnsFalse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID recommendationId = UUID.randomUUID();

        // Act & Assert
        assertThat(dynamicRecommendationRepository.isAlreadyIssued(userId, recommendationId)).isFalse();
    }

    @Test
    void getIssuedRecommendations_ReturnsList() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID recommendationId = UUID.randomUUID();
        String productName = "Test Product";
        String productText = "Test Text";

        dynamicRecommendationRepository.save(userId, recommendationId, productName, productText);

        // Act
        List<IssuedRecommendationDTO> issued = dynamicRecommendationRepository.getIssuedRecommendations(userId);

        // Assert
        assertThat(issued).hasSize(1);
        assertThat(issued.get(0).getUserId()).isEqualTo(userId);
        assertThat(issued.get(0).getRecommendationId()).isEqualTo(recommendationId);
        assertThat(issued.get(0).getIssuedAt()).isNotNull();
    }

    @Test
    void clearUserRecommendations_RemovesAllUserRecommendations() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID recommendationId = UUID.randomUUID();
        String productName = "Test Product";
        String productText = "Test Text";

        dynamicRecommendationRepository.save(userId, recommendationId, productName, productText);
        assertThat(dynamicRecommendationRepository.isAlreadyIssued(userId, recommendationId)).isTrue();

        // Act
        dynamicRecommendationRepository.clearUserRecommendations(userId);

        // Assert
        assertThat(dynamicRecommendationRepository.isAlreadyIssued(userId, recommendationId)).isFalse();
    }
}
