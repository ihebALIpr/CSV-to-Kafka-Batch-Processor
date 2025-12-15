package com.speegar.kafka.consumer;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StaticBeansTools {

	private static ObjectMapper objectMapper;

	public StaticBeansTools(ObjectMapper objectMapper) {
		StaticBeansTools.objectMapper = objectMapper;
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

}
