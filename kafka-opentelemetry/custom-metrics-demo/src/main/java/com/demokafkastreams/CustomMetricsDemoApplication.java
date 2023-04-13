package com.demokafkastreams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
public class CustomMetricsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomMetricsDemoApplication.class, args);
	}


}
