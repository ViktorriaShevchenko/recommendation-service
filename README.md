#  Recommendation Service

Микросервис для персональных рекомендаций кредитных продуктов на основе анализа транзакций пользователей с интеграцией Telegram бота.

---

##  Функциональность

Сервис анализирует финансовое поведение пользователей и рекомендует подходящие банковские продукты согласно бизнес-правилам.

### **Рекомендации через Telegram бота**
- **Команда `/start`** - приветствие и инструкции
- **Команда `/recommend username`** - получение персонализированных рекомендаций
- **Форматированные ответы** с именем пользователя и списком продуктов
- **Обработка ошибок** для несуществующих пользователей

###  Правила рекомендаций

| Название | ID правила | Условия |
|----------|------------|---------|
| **Invest 500** | `147f6a0f-3b91-413b-ab99-87f081d60d5a` | • Использует продукты типа **DEBIT**<br>• **НЕ** использует продукты типа **INVEST**<br>• Сумма пополнений **SAVING** > 1 000 ₽ |
| **Top Saving** | `59efc529-2fff-41af-baff-90ccd7402925` | • Использует продукты типа **DEBIT**<br>• Сумма пополнений **DEBIT** ≥ 50 000 ₽ **ИЛИ** **SAVING** ≥ 50 000 ₽<br>• Пополнения **DEBIT** > траты **DEBIT** |
| **Простой кредит** | `ab138afb-f3ba-4a93-b74f-0fcee86d447f` | • **НЕ** использует продукты типа **CREDIT**<br>• Пополнения **DEBIT** > траты **DEBIT**<br>• Траты **DEBIT** > 100 000 ₽ |

**Динамические правила (CRUD API)**
Гибкая система для добавления, изменения и удаления правил рекомендаций через REST API.
---

#### **Статистика срабатываний правил**
- Автоматический учет срабатываний динамических правил
- Атомарное увеличение счетчиков (защита от race condition)
- Просмотр статистики через REST API

##  Технологический стек

- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Data JPA +  Spring Data JDBC** 
- **PostgreSQL** (динамические правила) **+ H2 Database** (транзакции пользователей)
- **Liquibase** (миграции базы данных)
- **Caffeine** (кеширование запросов)
- **Telegram Bots API** (интеграция с Telegram)
- **Maven**
- **JUnit 5** + **Mockito**

---

##  Структура проекта

```
src/main/java/com/starbank/recommendation_service/
├── bot/                    
│   ├── RecommendationTelegramBot.java # Telegram бот
├── configuration/                     # Конфигурация БД и приложения
│   ├── DynamicRulesDataSourceConfiguration.java
│   ├── RecommendationsDataSourceConfiguration.java
│   └── TelegramBotConfig # Конфигурация Telegram бота
├── controller/                       # REST API эндпоинты
│   ├── DynamicRuleController.java    # CRUD для динамических правил
│   ├── ManagementController.java     # Управление (кеши, info)
│   ├── RecommendationController.java # Получение рекомендаций
│   └── RuleStatsController.java     # Статистика правил
├── dto/                             # Data Transfer Objects
├── entity/                          # JPA сущности
├── repository/                      # Репозитории для работы с БД
│   ├── dynamic/                     # Для PostgreSQL (динамические правила)
│   └── recommendations/             # Для H2 (транзакции)
├── service/                         # Бизнес-логика
│   ├── dynamic/
│   ├── rule/                        # Фиксированные правила
│   ├── RecommendationService.java   # Основная логика рекомендаций
│   ├── RuleStatisticService.java    # Управление статистикой
│   ├── CacheManagementService.java  # Управление кешами
    ├── UserService
│   └── RecommendationService.java   # Основная логика рекомендаций
└── RecommendationServiceApplication.java
```

---

##  Быстрый старт

### Предварительные требования
- **Java 17** или выше
- **Maven 3.6+**
- **PostgreSQL** (версия 12+)
- **Telegram бот токен**
- Файл базы данных `transaction.mv.db` в корне проекта

##  Настройка базы данных
### Создайте базу данных PostgreSQL:

```bash
CREATE DATABASE recommendation_service;
```
### Основные настройки в application.properties:
```bash
# PostgreSQL для динамических правил
spring.datasource.dynamic-rules.url=jdbc:postgresql://localhost:5432/recommendation_rules_db
spring.datasource.dynamic-rules.username=user
spring.datasource.dynamic-rules.password=user

# H2 для транзакций (read-only)
application.recommendations-db.url=jdbc:h2:file:./transaction;MODE=PostgreSQL

# Telegram бот
telegram.bot.enabled=true
```

### Сборка и запуск

```bash
# Клонирование и переход в директорию проекта
git clone <repository-url>
cd recommendation-service

# Сборка проекта с генерацией build-info
mvn clean package

# Запуск приложения
java -jar target/recommendation-service-0.0.1-SNAPSHOT.jar

# Или запуск через Maven
mvn spring-boot:run
```

Приложение будет доступно по адресу: **http://localhost:8080**
Важно: Это REST API сервис, а не веб-сайт. После запуска используйте API endpoints, описанные ниже.
---

##  API Endpoints

### Telegram бот

/start - приветствие и инструкции

/recommend username - получение рекомендаций для пользователя

Пример: /recommend ivanov

### Получение рекомендаций для пользователя

**Endpoint:**  
`GET /recommendation/{userId}`

**Пример запроса:**
```bash
curl "http://localhost:8080/recommendation/cd515076-5d8a-44be-930e-8d4fcb79f42d"
```
### Статистика срабатываний правил
Endpoint: GET /rule/stats

**Пример ответа:**
```json
{
  "stats": [
    {
      "rule_id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
      "count": 5
    },
    {
      "rule_id": "c3d4e5f6-7890-1234-5678-9abcdef01234",
      "count": 0
    }
  ]
}
```
## Управление кешами
Endpoint: POST /management/clear-caches

**Пример ответа:**
```json
{
  "success": true,
  "message": "All caches cleared successfully",
  "caches_cleared": 5
}
```
## Информация о сборке
Endpoint: GET /management/info
**Пример ответа:**
```json
{
  "name": "recommendation-service",
  "version": "1.0.0"
}
```

## Управление динамическими правилами
- POST /rule - создание нового правила
- GET /rule - получение всех правил
- DELETE /rule/{productId} - удаление правила

## Поддерживаемые типы запросов в динамических правилах

---
| Тип запроса                              | Описание                             | Пример аргументов                                                                                                                                         |
|------------------------------------------|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| USER_OF                                  | Пользователь имеет продукт | `["DEBIT"]`                           |
| ACTIVE_USER_OF                           | Активный пользователь продукта (≥5 транзакций) | `["DEBIT"]` |
| TRANSACTION_SUM_COMPARE                  | Сравнение суммы транзакций |             `["DEBIT", "DEPOSIT", ">", "1000"]`                   |
| TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW | Сравнение пополнений и трат |   `["DEBIT", ">"]`                              |


##  Тестирование

### Запуск тестов
```bash
# Все тесты
mvn test

# Только unit-тесты
mvn test -Dtest="*UnitTest"

# Только интеграционные тесты
mvn test -Dtest="*IntegrationTest"

# С генерацией отчета о покрытии
mvn clean verify
```

### Типы тестов
-  **Интеграционные тесты** контроллера
-  **Unit-тесты** с моками (Mockito)
-  **Тесты репозиториев** с тестовыми БД
-  **Тесты бизнес-логики** правил рекомендаций
-  **Тесты Telegram бота** (моки)
-  **Тесты атомарности** инкремента статистики
-  **Тесты многопоточности**
-  **Тесты кеширования**
-  **Тесты динамических правил**

---
## Миграции базы данных
Проект использует Liquibase для управления миграциями PostgreSQL:

```bash
yaml
db/changelog/
├── db.changelog-master.yml
├── 001-initial-schema.yml          # Таблица динамических правил
├── 002-create-issued-recommendations-table.yml # Выданные рекомендации
└── 003-create-rule-statistics-table.yml        # Статистика срабатываний
```
Автоматическое применение миграций при запуске приложения.
---

##  Команда разработки

| Роль | Разработчик | Область ответственности                                                  |
|------|-------------|--------------------------------------------------------------------------|
| **Backend разработка** | Шевченко Виктория | Планирование, кеширование, статистика, тестирование, документация        |
| **Backend разработка** | Шишкин Денис | Динамические правила, PostgreSQL, JPA, Liquibase, CRUD API, Telegram бот |

## Особенности реализации
- **Двойная БД архитектура** - разделение ответственности между БД
- **Гибкие динамические правила** - JSONB хранение условий в PostgreSQL
- **Интеллектуальное кеширование** - Caffeine для часто запрашиваемых данных
- **Предотвращение дублирования** - история выданных рекомендаций
- **Валидация входных данных** - проверка правил на корректность
- **Расширяемость** - легкое добавление новых типов запросов
- **Telegram интеграция** - асинхронная обработка сообщений, форматированные ответы с персонализацией, обработка ошибок пользовательского ввода
- **Система статистики** - Атомарные инкременты счетчиков (SQL SET count = count + 1), гарантия целостности при параллельных запросах, автоматическое создание статистики для новых правил
- **Кеширование** - многоуровневое кеширование в RecommendationsRepository, автоматическая инвалидация через TTL (10 минут), ручная очистка через API endpoint

---
## Требования к окружению
- Java: 17+
- PostgreSQL: 12+
- Maven: 3.6+
- Память: Минимум 512MB JVM heap
- Диск: 100MB свободного места
- Сеть: Доступ к Telegram API (api.telegram.org)
---
## Безопасность
- Валидация всех входных данных(JSON Schema, Bean Validation)
- Защита от SQL-инъекций через JPA/Hibernate
- Логирование ключевых операций с конфиденциальными данными
- Проверка уникальности правил и продуктов

---

## Мониторинг и администрирование
### Доступные endpoints для мониторинга:
- GET /management/info - информация о версии
- GET /rule/stats - статистика использования правил
- POST /management/clear-caches - управление кешами

---
## Развертывание
### Docker (опционально)
dockerfile
```
FROM openjdk:17-jdk-slim
COPY target/recommendation-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
### Конфигурация для продакшена
properties
```
## Включить все health checks
management.endpoints.web.exposure.include=health,info,metrics

# Настройки пула соединений
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Отключить H2 console в продакшене
spring.h2.console.enabled=false

```

## Лицензия

Проект разработан в рамках учебного задания. Все права защищены.

---
*Последнее обновление: январь 2026*
Версия: 3.0 (с Telegram ботом, статистикой и production-ready features)