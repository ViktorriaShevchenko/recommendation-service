package com.starbank.recommendation_service.repository.dynamic;

import com.starbank.recommendation_service.entity.dynamic.IssuedRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssuedRecommendationRepository extends JpaRepository<IssuedRecommendation, UUID> {

    Optional<IssuedRecommendation> findByUserIdAndRecommendationId(UUID userId, UUID recommendationId);

    boolean existsByUserIdAndRecommendationId(UUID userId, UUID recommendationId);

    List<IssuedRecommendation> findByUserId(UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM IssuedRecommendation ir WHERE ir.userId = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM IssuedRecommendation ir WHERE ir.recommendationId = :recommendationId")
    int deleteByRecommendationId(@Param("recommendationId") UUID recommendationId);
}
