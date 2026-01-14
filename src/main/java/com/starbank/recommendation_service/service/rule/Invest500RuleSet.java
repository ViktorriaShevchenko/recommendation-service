package com.starbank.recommendation_service.service.rule;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public Invest500RuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        boolean isEligible = repository.hasDebitProduct(userId)
                             && repository.hasNoInvestProducts(userId)
                             && repository.hasSavingDepositsOver1000(userId);

        if (isEligible) {
            return Optional.of(new RecommendationDTO(
                    "Invest 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным " +
                    "счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и " +
                    "начните инвестировать с умом. Пополните счет до конца года и " +
                    "получите выгоду в виде вычета на взнос в следующем налоговом периоде. " +
                    "Не упустите возможность разнообразить свой портфель, снизить риски и " +
                    "следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и " +
                    "станьте ближе к финансовой независимости!"
            ));
        }

        return Optional.empty();
    }
}
