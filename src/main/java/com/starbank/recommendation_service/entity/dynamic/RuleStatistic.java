package com.starbank.recommendation_service.entity.dynamic;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rule_statistics")
public class RuleStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private DynamicRecommendationRule rule;

    @Column(name = "count", nullable = false)
    private Long count = 0L;

    @Column(name = "last_triggered_at")
    private LocalDateTime lastTriggeredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public RuleStatistic() {
    }

    public RuleStatistic(DynamicRecommendationRule rule) {
        this.rule = rule;
        this.count = 0L;
    }

    public RuleStatistic(DynamicRecommendationRule rule, Long count) {
        this.rule = rule;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DynamicRecommendationRule getRule() {
        return rule;
    }

    public void setRule(DynamicRecommendationRule rule) {
        this.rule = rule;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public LocalDateTime getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) {
        this.lastTriggeredAt = lastTriggeredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void increment() {
        this.count++;
        this.lastTriggeredAt = LocalDateTime.now();
    }


}
