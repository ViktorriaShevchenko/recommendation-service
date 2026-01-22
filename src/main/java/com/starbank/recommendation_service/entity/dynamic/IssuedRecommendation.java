package com.starbank.recommendation_service.entity.dynamic;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "issued_recommendations",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_recommendation",
                columnNames = {"user_id", "recommendation_id"}
        ))
public class IssuedRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "recommendation_id", nullable = false)
    private UUID recommendationId;

    @CreationTimestamp
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_text", columnDefinition = "TEXT")
    private String productText;

    public IssuedRecommendation() {
    }

    public IssuedRecommendation(UUID userId, UUID recommendationId, String productName, String productText) {
        this.userId = userId;
        this.recommendationId = recommendationId;
        this.productName = productName;
        this.productText = productText;
        this.issuedAt = LocalDateTime.now();
    }

    public IssuedRecommendation(UUID id, UUID userId, UUID recommendationId, LocalDateTime issuedAt, String productName, String productText) {
        this.id = id;
        this.userId = userId;
        this.recommendationId = recommendationId;
        this.issuedAt = issuedAt;
        this.productName = productName;
        this.productText = productText;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(UUID recommendationId) {
        this.recommendationId = recommendationId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public static IssuedRecommendation create(UUID userId, UUID recommendationId, String productName, String productText) {
        return new IssuedRecommendation(userId, recommendationId, productName, productText);
    }
}
