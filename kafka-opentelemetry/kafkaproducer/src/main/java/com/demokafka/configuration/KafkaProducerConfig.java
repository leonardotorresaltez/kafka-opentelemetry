package com.demokafka.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.demokafkastreams.Book;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class KafkaProducerConfig<V> {


    @Autowired
    public KafkaProducerConfig() {
        super();
    }
    


    @Bean
    public KafkaTemplate<String, Book> kafkaTemplateBook(ProducerFactory<String, Book> producerFactory) {
    	return createKafkaTemplate(producerFactory);    
    }    
    
   
    
 
    
    private KafkaTemplate createKafkaTemplate(ProducerFactory producerFactory){
        Map<String, Object> configOverrides1 = new HashMap<>();
        configOverrides1.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configOverrides1.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        return new KafkaTemplate<>(producerFactory, configOverrides1);
    	
    }
    



}
