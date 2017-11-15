package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.job.DefaultJobRepository;
import org.metplus.curriculum.database.repository.JobDocumentRepository;
import org.metplus.curriculum.domain.job.CreateJob;
import org.metplus.curriculum.domain.job.UpdateJob;
import org.metplus.curriculum.process.JobCruncher;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public JobsController jobsController(
        CreateJob createJob,
        UpdateJob updateJob,
        JobCruncher jobCruncher
    ) {
        return new JobsController(
            createJob,
            updateJob,
            jobCruncher
        );
    }

    @Bean
    public CreateJob createJob(DefaultJobRepository jobsRepository) {
        return new CreateJob(jobsRepository);
    }

    @Bean
    public UpdateJob updateJob(DefaultJobRepository jobsRepository) {
        return new UpdateJob(jobsRepository);
    }

    @Bean
    public DefaultJobRepository defaultJobRepository(JobDocumentRepository jobRepository) {
        return new DefaultJobRepository(jobRepository);
    }
}
