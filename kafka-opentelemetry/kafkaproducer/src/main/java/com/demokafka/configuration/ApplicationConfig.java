package com.demokafka.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.properties")
@Data
public class ApplicationConfig {

    private String myBookTopic;




}
