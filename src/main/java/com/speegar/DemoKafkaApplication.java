package com.speegar;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@EnableBatchProcessing
 @SpringBootApplication
public class DemoKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoKafkaApplication.class, args);
	}

}
