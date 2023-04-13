package com.demokafkastreams.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.properties")
@Data
public class ApplicationConfig {

    private String bookTopic;
    private String categoryTopic;
    private String stateStoreCategoryTopic;
    private String bookEnrichedTopic;



}
