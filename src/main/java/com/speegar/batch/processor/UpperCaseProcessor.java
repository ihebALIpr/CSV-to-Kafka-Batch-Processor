package com.speegar.batch.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.speegar.MessageWebSocketHandler;
import com.speegar.batch.model.CsvRecord;

@EnableBatchProcessing
@Component
public class UpperCaseProcessor implements ItemProcessor<CsvRecord, CsvRecord> {
	@Autowired
	private MessageWebSocketHandler webSocketHandler;

	static Map<String, String> codeNumeberCountry = Map.of("tunisia", "+216", "france", "+33", "algeria","+213"
			, "italy","+39"
				);

	@Override
	public CsvRecord process(CsvRecord record) throws Exception {
		CsvRecord transformed = new CsvRecord();
		transformed.setId(record.getId().toUpperCase());
		transformed.setName(record.getName().toUpperCase());
		transformed.setDescription(record.getDescription().toUpperCase());
		var country = record.getCountry();
		var prefix = codeNumeberCountry.get(country);
		transformed.setCountry(record.getCountry().toUpperCase());
		transformed.setPhoneNumber(prefix + record.getPhoneNumber());

		System.out.println("Processing: " + record + " -> " + transformed);
		webSocketHandler.broadcastMessage("message",
				Map.of("id", transformed.getId(), "country", transformed.getCountry(), "name", transformed.getName(),
						"description", transformed.getDescription(), "phoneNumber", transformed.getPhoneNumber(),
						"timestamp", LocalDateTime.now() .toString()));

		return transformed;
	}
}
