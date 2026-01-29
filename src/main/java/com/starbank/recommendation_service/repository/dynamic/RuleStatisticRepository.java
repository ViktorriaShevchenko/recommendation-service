package com.starbank.recommendation_service.repository.dynamic;

import com.starbank.recommendation_service.entity.dynamic.RuleStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleStatisticRepository extends JpaRepository<RuleStatistic, Long> {

    Optional<RuleStatistic> findByRuleId(UUID ruleId);

    @Modifying
    @Query("UPDATE RuleStatistic rs SET rs.count = rs.count + 1, rs.lastTriggeredAt = CURRENT_TIMESTAMP WHERE rs.rule.id = :ruleId")
    int incrementCountByRuleId(@Param("ruleId") UUID ruleId);

    @Modifying
    @Query("DELETE FROM RuleStatistic rs WHERE rs.rule.id = :ruleId")
    int deleteByRuleId(@Param("ruleId") UUID ruleId);

    boolean existsByRuleId(UUID ruleId);
}
