//package com.speegar.batch.job;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.file.FlatFileItemReader;
// import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//
//
//@Configuration
//public class CleaningJobConfig {
//
//    @Bean
//       Job cleaningJob(JobRepository repo, Step cleaningStep) {
//        return new JobBuilder("cleaningJob", repo)
//                .start(cleaningStep)
//                .build();
//    }
//
//    @Bean
//      Step cleaningStep(
//            JobRepository repo,
//            PlatformTransactionManager tx,
//            FlatFileItemReader<Person> reader,
//            PersonItemProcessor processor,
//            KafkaPersonWriter writer
//    ) {
//        return new StepBuilder("cleaningStep", repo)
//                .<Person, Person>chunk(5)
//                .reader(reader)
//                .processor(processor)
//                .writer(writer)
//                .transactionManager(tx)
//                .build();
//    }
//}
