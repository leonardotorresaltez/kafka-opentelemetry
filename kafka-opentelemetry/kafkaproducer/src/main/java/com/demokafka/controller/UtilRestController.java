package com.demokafka.controller;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demokafka.configuration.ApplicationConfig;
import com.demokafka.services.Producer;
import com.demokafkastreams.Book;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@AllArgsConstructor
public class UtilRestController {

    private final Producer producer;

    
    @GetMapping("/produce-fake-data")
    public void addMessage() {
        log.info("received /produce-fake-data ");

     
        Book message = Book.newBuilder()
        		.setIsbn("0001")
        		.setAuthor("Julio Velazques")
        		.setCategoryId("001")
        		.setPublisher("publisher1")
        		.setTitle("title1")
        		.build();       									
        producer.sendMessageBook(message.getIsbn(), message);
    }
    
//    
//    @GetMapping("/populate-ktables")
//    public void populateKtables() {
//        log.info("received /populate-ktables ");
//
//     
//        Category message = Category.newBuilder()
//        		.setCategoryId("001")
//        		.setCategoryName("fiction")
//        		.setDescription("description 1")
//        		.build();       									
//        producer.sendMessageCategory(message.getCategoryId(), message);
//        
//        
//               
//        
//    }   
//    
//    @Autowired
//    private  StreamsBuilderFactoryBean streamsBuilderFactoryBean;
//    
//    @GetMapping("/showRawKafkaStreamMetrics")
//    public String showMetrics() {
//
//        var kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
//        if (Objects.isNull(kafkaStreams)) {
//            return "No metrics logged. KafkaStreams is null";
//        }
//
//    	Map<MetricName, ? extends Metric>  metrics = kafkaStreams.metrics();
//    	 log.info("KakfaStream metrics: {}", metrics);
//    	 for (Map.Entry<MetricName,?> entry : metrics.entrySet()) 
//    	 {  
//    		 org.apache.kafka.common.metrics.KafkaMetric valueInside = (org.apache.kafka.common.metrics.KafkaMetric)entry.getValue();
//    		 log.info("Item: " + entry.getKey() + ", Value: " + valueInside.metricValue());   
//    	 }  
//
//        return "Raw Kafka metrics logged";
//    }    

}
