package com.cgzt.coinscode.core.config.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GlobalBatchConfig {
    @Bean
    Job multipleStepJob(final JobRepository jobRepository,
                        final Step createUserAccountStep,
                        final Step createCoinsStep) {
        return new JobBuilder("multipleStepJob", jobRepository)
                .start(createUserAccountStep)
                .next(createCoinsStep)
                .build();
    }
}
