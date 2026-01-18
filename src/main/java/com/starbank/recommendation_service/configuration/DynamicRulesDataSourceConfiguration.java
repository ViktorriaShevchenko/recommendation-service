package com.starbank.recommendation_service.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.starbank.recommendation_service.repository.dynamic",
        entityManagerFactoryRef = "dynamicRulesEntityManagerFactory",
        transactionManagerRef = "dynamicRulesTransactionManager"
)
public class DynamicRulesDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.dynamic-rules")
    public DataSourceProperties dynamicRulesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dynamicRulesDataSource() {
        return dynamicRulesDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dynamicRulesEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dynamicRulesDataSource())
                .packages("com.starbank.recommendation_service.entity.dynamic")
                .persistenceUnit("dynamicRules")
                .build();
    }

    @Bean
    public PlatformTransactionManager dynamicRulesTransactionManager(
            LocalContainerEntityManagerFactoryBean dynamicRulesEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(dynamicRulesEntityManagerFactory.getObject()));
    }
    @Bean
    public SpringLiquibase dynamicRulesLiquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dynamicRulesDataSource());
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yml");
        liquibase.setContexts("development");
        liquibase.setShouldRun(true);
        liquibase.setDefaultSchema("public");
        return liquibase;
    }
}