//package com.demokafka.controller;
//
//import java.util.Map;
//import java.util.Objects;
//
//import org.apache.kafka.common.Metric;
//import org.apache.kafka.common.MetricName;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
//import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
//import org.springframework.kafka.config.StreamsBuilderFactoryBean;
//import org.springframework.stereotype.Component;
//
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//@Component
//@Endpoint(id="topology")
//public class TopologyEndPoint {
//
//    @Autowired
//    private  StreamsBuilderFactoryBean streamsBuilderFactoryBean;	
//	
//    @ReadOperation
//    public String topology() {
//        var kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
//        if (Objects.isNull(kafkaStreams)) {
//            return "No metrics logged. KafkaStreams is null";
//        }
//
//    	Map<MetricName, ? extends Metric>  metrics = kafkaStreams.metrics();
//    	 for (Map.Entry<MetricName,?> entry : metrics.entrySet()) 
//    	 {  
//    		 org.apache.kafka.common.metrics.KafkaMetric valueInside = (org.apache.kafka.common.metrics.KafkaMetric)entry.getValue();
//    		 if ( entry.getKey().name().equals("topology-description")) {
//    			 Object value = valueInside.metricValue();
//    			 log.info("Topology: {}", value.toString());
//    			 return value.toString();
//    		 }    		 
//    	 }
//    	 return "not found";
//    }
//
//  
//}