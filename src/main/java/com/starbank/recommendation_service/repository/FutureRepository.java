package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.dto.IssuedRecommendationDTO;
import com.starbank.recommendation_service.entity.dynamic.IssuedRecommendation;
import com.starbank.recommendation_service.repository.dynamic.IssuedRecommendationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FutureRepository {

    private final IssuedRecommendationRepository issuedRecommendationRepository;

    public FutureRepository(IssuedRecommendationRepository issuedRecommendationRepository) {
        this.issuedRecommendationRepository = issuedRecommendationRepository;
    }

    /**
     * Сохраняет выданную рекомендацию
     */
    @Transactional
    public void save(UUID userId, UUID recommendationId, String productName, String productText) {
        try {
            // Проверяем, не была ли уже выдана эта рекомендация
            if (isAlreadyIssued(userId, recommendationId)) {
                return;
            }

            IssuedRecommendation issuedRecommendation = IssuedRecommendation.create(
                    userId, recommendationId, productName, productText);

            issuedRecommendationRepository.save(issuedRecommendation);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save issued recommendation", e);
        }
    }

    /**
     * Проверяет, была ли рекомендация уже выдана пользователю
     */
    public boolean isAlreadyIssued(UUID userId, UUID recommendationId) {
        try {
            return issuedRecommendationRepository.existsByUserIdAndRecommendationId(userId, recommendationId);
        } catch (Exception e) {
            return false; // В случае ошибки считаем, что рекомендация не выдана
        }
    }

    /**
     * Получает список всех выданных рекомендаций для пользователя
     */
    public List<IssuedRecommendationDTO> getIssuedRecommendations(UUID userId) {
        try {
            return issuedRecommendationRepository.findByUserId(userId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get issued recommendations", e);
        }
    }

    /**
     * Удаляет все выданные рекомендации пользователя
     */
    @Transactional
    public void clearUserRecommendations(UUID userId) {
        try {
            issuedRecommendationRepository.deleteByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear user recommendations", e);
        }
    }

    /**
     * Удаляет все выданные рекомендации по ID рекомендации
     */
    @Transactional
    public void clearRecommendationIssues(UUID recommendationId) {
        try {
            issuedRecommendationRepository.deleteByRecommendationId(recommendationId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear recommendation issues", e);
        }
    }

    private IssuedRecommendationDTO convertToDTO(IssuedRecommendation entity) {
        return new IssuedRecommendationDTO(
                entity.getUserId(),
                entity.getRecommendationId(),
                entity.getIssuedAt()
        );
    }
}
