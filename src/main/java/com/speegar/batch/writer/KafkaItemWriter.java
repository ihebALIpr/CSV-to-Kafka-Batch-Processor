package com.speegar.batch.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speegar.batch.model.CsvRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaItemWriter implements ItemWriter<CsvRecord> {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private static final Logger logger = LoggerFactory.getLogger(KafkaItemWriter.class);
	@Value("${kafka.topic.name:demo-topic}")
	private String topicName;

	public KafkaItemWriter(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void write(Chunk<? extends CsvRecord> chunk) throws Exception {
		logger.info("Writing {} records to Kafka topic: {}", chunk.size(), topicName);

		for (CsvRecord record : chunk) {
			String message = objectMapper.writeValueAsString(record);
			kafkaTemplate.send(topicName, record.getId(), message).whenComplete((result, ex) -> {
				if (ex == null) {
					logger.info("Sent to Kafka successfully: {}", message);
				} else {
					logger.error("Failed to send to Kafka: {}", message, ex);
				}
			});
		}
	}

}