package com.starbank.recommendation_service.service.rule;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.entity.ProductType;
import com.starbank.recommendation_service.entity.TransactionType;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    private static final UUID INVEST_500_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");

    public Invest500RuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        boolean isEligible = repository.hasProduct(userId, ProductType.DEBIT)
                             && !repository.hasProduct(userId, ProductType.INVEST)
                             && (repository.transactionSumAndTypeForProductType(userId, ProductType.SAVING, TransactionType.DEPOSIT) > 1000);

        if (isEligible) {
            return Optional.of(new RecommendationDTO(
                    INVEST_500_ID,
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
