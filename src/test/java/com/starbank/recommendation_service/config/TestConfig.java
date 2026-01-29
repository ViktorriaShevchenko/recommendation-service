package com.starbank.recommendation_service.config;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public BuildProperties buildProperties() {
        Properties props = new Properties();
        props.setProperty("artifact", "recommendation-service");
        props.setProperty("name", "recommendation-service");
        props.setProperty("version", "1.0.0");
        props.setProperty("group", "com.starbank");
        props.setProperty("time", "2024-01-29T14:30:00Z");

        return new BuildProperties(props);
    }
}
