package com.speegar;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.speegar.batch.model.CsvRecord;
import com.speegar.batch.processor.UpperCaseProcessor;
import com.speegar.batch.writer.KafkaItemWriter;

@EnableBatchProcessing
@Configuration
public class BatchConfig {

    
    @Bean
     FlatFileItemReader<CsvRecord> reader() {
        return new FlatFileItemReaderBuilder<CsvRecord>()
                .name("csvReader")
                .resource(new ClassPathResource("data.csv"))
                .delimited()
                .names("id", "name", "description","country","phoneNumber")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(CsvRecord.class);
                }})
                .linesToSkip(1) // Skip header
                .build();
    }
    @Bean
      Step csvToKafkaStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                FlatFileItemReader<CsvRecord> reader,
                                UpperCaseProcessor processor,
                                KafkaItemWriter writer) {
        return new StepBuilder("csvToKafkaStep", jobRepository)
                .<CsvRecord, CsvRecord>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
      Job csvToKafkaJob(JobRepository jobRepository, Step csvToKafkaStep) {
        return new JobBuilder("csvToKafkaJob", jobRepository)
                .start(csvToKafkaStep)
                .build();
    }
}