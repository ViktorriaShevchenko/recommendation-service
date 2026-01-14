package com.starbank.recommendation_service.service.rule;

import com.starbank.recommendation_service.dto.RecommendationDTO;
import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    private static final UUID SIMPLE_CREDIT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");

    public SimpleCreditRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        boolean isEligible = repository.hasNoCreditProducts(userId)
                             && repository.hasDebitDepositsGreaterThanExpenses(userId)
                             && repository.hasDebitExpensesOver100000(userId);

        if (isEligible) {
            return Optional.of(new RecommendationDTO(
                    SIMPLE_CREDIT_ID,
                    "Простой кредит",
                    "Откройте мир выгодных кредитов с нами!\n" +
                    "\n" +
                    "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда " +
                    "наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие " +
                    "процентные ставки, гибкие условия и индивидуальный подход к каждому " +
                    "клиенту.\n" +
                    "\n" +
                    "Почему выбирают нас:\n" +
                    "\n" +
                    "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс " +
                    "рассмотрения заявки занимает всего несколько часов.\n" +
                    "\n" +
                    "Удобное оформление. Подать заявку на кредит можно онлайн на нашем " +
                    "сайте или в мобильном приложении.\n" +
                    "\n" +
                    "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные " +
                    "цели: покупку недвижимости, автомобиля, образование, лечение и многое " +
                    "другое.\n" +
                    "\n" +
                    "Не упустите возможность воспользоваться выгодными условиями кредитования " +
                    "от нашей компании!"
            ));
        }

        return Optional.empty();
    }
}
