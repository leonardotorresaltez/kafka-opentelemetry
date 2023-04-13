package com.demokafkastreams.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties(prefix = "app.kafka.custom")
@Configuration
@Getter
@Setter
public class KafkaClientCustomProperties {

	private String maxPollRecords;
	private String standByReplicas;
	private String sessionTimeOut;
	private String heartbeatInterval;
	private String linger;
	private String requestTimeout;
	private String numThreads;
	private Map<String, String> changeLogConfig;
	private boolean staticMembership;
	private long logAdditionalRetentionTime;
	private long cacheMaxBytesBuffering;
	
}
