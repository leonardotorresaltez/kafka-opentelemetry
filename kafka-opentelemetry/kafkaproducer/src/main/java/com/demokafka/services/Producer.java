package com.demokafka.services;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.demokafka.configuration.ApplicationConfig;
import com.demokafkastreams.Book;

import lombok.AllArgsConstructor;

@Log4j2
@Component
public class Producer {

	@Autowired
    private  KafkaTemplate<String, Book> kafkaTemplateBook;
	
	
	

	@Autowired
	private ApplicationConfig applicationConfig; 

    public void sendMessageBook(String key, Book message) {
    	kafkaTemplateBook.send(applicationConfig.getMyBookTopic(), key, message)
                .addCallback(
                        result -> log.info("Message sent to topic: {}", message),
                        ex -> log.error("Failed to send message", ex)
                );
    }
  

      

}
