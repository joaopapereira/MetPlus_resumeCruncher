package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.job.DefaultJobRepository;
import org.metplus.curriculum.database.repository.JobDocumentRepository;
import org.metplus.curriculum.domain.job.CreateJob;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public JobsController jobsController(CreateJob<ResponseEntity<GenericAnswer>> createJob) {
        return new JobsController(createJob);
    }

    @Bean
    public CreateJob<ResponseEntity<GenericAnswer>> createJob(DefaultJobRepository jobsRepository) {
        return new CreateJob<>(jobsRepository);
    }

    @Bean
    public DefaultJobRepository defaultJobRepository(JobDocumentRepository jobRepository) {
        return new DefaultJobRepository(jobRepository);
    }
}
