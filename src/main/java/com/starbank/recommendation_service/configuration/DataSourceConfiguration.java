package com.starbank.recommendation_service.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfiguration {

    @Primary
    @Bean(name = "recommendationsDataSource")
    @ConfigurationProperties(prefix = "application.recommendations-db")
    public HikariDataSource recommendationsDataSource() {
        return new HikariDataSource();
    }

    @Primary
    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate() {
        return new JdbcTemplate(recommendationsDataSource());
    }

    //При необходимости наша БД с возможностью записи
    /*
    @Bean(name = "futureDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.future")
    public DataSource futureDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "futureJdbcTemplate")
    public JdbcTemplate futureJdbcTemplate(
            @Qualifier("futureDataSource") DataSource futureDataSource) {
        return new JdbcTemplate(futureDataSource);
    }
    */
}
