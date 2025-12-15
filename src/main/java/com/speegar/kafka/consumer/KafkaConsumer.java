package com.speegar.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speegar.MessageWebSocketHandler;

@Service
public class KafkaConsumer {

	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

 
	public KafkaConsumer( ) {
	}

	@KafkaListener(topics = "demo-topic", groupId = "demo-group")
	public void consume(String message) {
		logger.info("[mocking another microservice to handle save]Consumed message from Kafka: {}", message);
		
	}

	@KafkaListener(topics = "demo-topic", groupId = "demo-group")
	public void listen(String message) {
		System.out.println("Received message: " + message);
	}
}
