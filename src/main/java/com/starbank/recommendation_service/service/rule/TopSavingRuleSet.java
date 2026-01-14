package com.starbank.recommendation_service.service.rule;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    private static final UUID TOP_SAVING_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");

    public TopSavingRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        boolean isEligible = repository.hasDebitProduct(userId)
                             && repository.hasDebitOrSavingDepositsOver50000(userId)
                             && repository.hasDebitDepositsGreaterThanExpenses(userId);

        if (isEligible) {
            return Optional.of(new RecommendationDTO(
                    TOP_SAVING_ID,
                    "Top Saving",
                    "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это " +
                    "уникальный банковский инструмент, который поможет вам легко и удобно " +
                    "накапливать деньги на важные цели. Больше никаких забытых чеков и " +
                    "потерянных квитанций — всё под контролем!\n" +
                    "\n" +
                    "Преимущества «Копилки»:\n" +
                    "\n" +
                    "Накопление средств на конкретные цели. Установите лимит и срок накопления, " +
                    "и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
                    "\n" +
                    "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте " +
                    "процесс накопления и корректируйте стратегию при необходимости.\n" +
                    "\n" +
                    "Безопасность и надежность. Ваши средства находятся под защитой банка, а " +
                    "доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
                    "\n" +
                    "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"
            ));
        }

        return Optional.empty();
    }
}