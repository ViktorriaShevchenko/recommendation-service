#  Recommendation Service

Микросервис для персональных рекомендаций кредитных продуктов на основе анализа транзакций пользователей.

---

##  Функциональность

Сервис анализирует финансовое поведение пользователей и рекомендует подходящие банковские продукты согласно бизнес-правилам.

###  Правила рекомендаций

| Название | ID правила | Условия |
|----------|------------|---------|
| **Invest 500** | `147f6a0f-3b91-413b-ab99-87f081d60d5a` | • Использует продукты типа **DEBIT**<br>• **НЕ** использует продукты типа **INVEST**<br>• Сумма пополнений **SAVING** > 1 000 ₽ |
| **Top Saving** | `59efc529-2fff-41af-baff-90ccd7402925` | • Использует продукты типа **DEBIT**<br>• Сумма пополнений **DEBIT** ≥ 50 000 ₽ **ИЛИ** **SAVING** ≥ 50 000 ₽<br>• Пополнения **DEBIT** > траты **DEBIT** |
| **Простой кредит** | `ab138afb-f3ba-4a93-b74f-0fcee86d447f` | • **НЕ** использует продукты типа **CREDIT**<br>• Пополнения **DEBIT** > траты **DEBIT**<br>• Траты **DEBIT** > 100 000 ₽ |

**Динамические правила (CRUD API)**
Гибкая система для добавления, изменения и удаления правил рекомендаций через REST API.
---

##  Технологический стек

- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Data JPA +  Spring Data JDBC** 
- **PostgreSQL** (динамические правила) **+ H2 Database** (транзакции пользователей)
- **Liquibase** (миграции базы данных)
- **Caffeine** (кеширование запросов)
- **Maven**
- **JUnit 5** + **Mockito**

---

##  Структура проекта

```
src/main/java/com/starbank/recommendation_service/
├── configuration/                    # Конфигурация БД и приложения
│   ├── DynamicRulesDataSourceConfiguration.java
│   └── RecommendationsDataSourceConfiguration.java
├── controller/                       # REST API эндпоинты
│   ├── DynamicRuleController.java    # CRUD для динамических правил
│   └── RecommendationController.java # Получение рекомендаций
├── dto/                             # Data Transfer Objects
├── entity/                          # JPA сущности
├── repository/                      # Репозитории для работы с БД
│   ├── dynamic/                     # Для PostgreSQL (динамические правила)
│   └── recommendations/             # Для H2 (транзакции)
├── service/                         # Бизнес-логика
│   ├── rule/                        # Фиксированные правила
│   └── RecommendationService.java   # Основная логика рекомендаций
└── RecommendationServiceApplication.java
```

---

##  Быстрый старт

### Предварительные требования
- **Java 17** или выше
- **Maven 3.6+**
- **PostgreSQL** (версия 12+)
- Файл базы данных `transaction.mv.db` в корне проекта

##  Настройка базы данных
### Создайте базу данных PostgreSQL:

```bash
CREATE DATABASE recommendation_service;
```
### Настройте подключение в application.yml:
```bash
spring:
datasource:
dynamic-rules:
url: jdbc:postgresql://localhost:5432/recommendation_service
username: postgres
password: password
driver-class-name: org.postgresql.Driver
```

### Сборка и запуск

```bash
# Клонирование и переход в директорию проекта
git clone <repository-url>
cd recommendation-service

# Сборка проекта
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

### Получение рекомендаций для пользователя

**Endpoint:**  
`GET /recommendation/{userId}`

**Пример запроса:**
```bash
curl "http://localhost:8080/recommendation/cd515076-5d8a-44be-930e-8d4fcb79f42d"
```

**Пример успешного ответа (200 OK):**
```json
{
  "user_id": "cd515076-5d8a-44be-930e-8d4fcb79f42d",
  "recommendations": [
    {
      "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
      "name": "Invest 500",
      "text": "Откройте свой путь к успеху..."
    },
    {
      "id": "c3d4e5f6-7890-1234-5678-9abcdef01234",
      "name": "Динамический продукт",
      "text": "Описание динамического продукта..."
    }
  ]
}
```
## Управление динамическими правилами
### Создание нового правила
**Endpoint:** POST /rule

Пример запроса:
```bash
{
   "product_name": "Премиальная карта",
   "product_id": "c3d4e5f6-7890-1234-5678-9abcdef01234",
   "product_text": "Описание премиальной карты...",
   "rule": [
     {
       "query": "USER_OF",
       "arguments": ["DEBIT"],
       "negate": false
     },
     {
       "query": "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
       "arguments": ["DEBIT", ">"],
       "negate": false
     }
   ]
}
```
### Получение всех правил
**Endpoint:** GET /rule

### Удаление правила
**Endpoint:** DELETE /rule/{productId}

## Поддерживаемые типы запросов в динамических правилах

---

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
-  **Тесты кеширования**
-  **Тесты динамических правил**

---
## Миграции базы данных
Проект использует Liquibase для управления миграциями PostgreSQL:

```bash
yaml
db/
└── changelog/
└── db.changelog-master.yml     # Главный файл миграций
```
Автоматическое применение миграций при запуске приложения.
---

##  Команда разработки

| Роль | Разработчик | Область ответственности                                    |
|------|-------------|------------------------------------------------------------|
| **Backend разработка** | Шевченко Виктория | Планирование, кеширование, тестирование, документация      |
| **Backend разработка** | Шишкин Денис | Динамические правила, PostgreSQL, JPA, Liquibase, CRUD API |

## Особенности реализации
- **Двойная БД архитектура** - разделение ответственности между БД
- **Гибкие динамические правила** - JSONB хранение условий в PostgreSQL
- **Интеллектуальное кеширование** - Caffeine для часто запрашиваемых данных
- **Предотвращение дублирования** - история выданных рекомендаций
- **Валидация входных данных** - проверка правил на корректность
- **Расширяемость** - легкое добавление новых типов запросов
---
## Требования к окружению
- Java: 17+
- PostgreSQL: 12+
- Maven: 3.6+
- Память: Минимум 512MB JVM heap
- Диск: 100MB свободного места
---
## Безопасность
- Валидация всех входных данных
- Защита от SQL-инъекций через JPA
- Логирование операций
- Проверка уникальности правил

---

## Лицензия

Проект разработан в рамках учебного задания. Все права защищены.

---
*Последнее обновление: январь 2026*
Версия: 2.0 (с динамическими правилами и PostgreSQL)