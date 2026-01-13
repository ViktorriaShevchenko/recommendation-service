package com.starbank.recommendation_service.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class OurFutureDataSourceConfiguration {
    //При необходимости наша БД с возможностью записи

    @Bean(name = "ourFutureDataSource")
    public DataSource ourFutureDataSource(
            @Value("${application.ourFuture-db.url}") String ourFutureUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(ourFutureUrl);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(20000);
        return dataSource;
    }


    @Primary
    @Bean(name = "ourFutureJdbcTemplate")
    public JdbcTemplate ourFutureJdbcTemplate(
            @Qualifier("ourFutureDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
