package org.metplus.curriculum.domain.job;

import org.metplus.curriculum.domain.Job;

public class CreateJob<T> {

    private JobRepository jobRepository;

    public CreateJob(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public T create(String jobId, String title, String description, ResultHandler<T> resultHandler) {
        Job existingJob = jobRepository.findById(jobId);
        if (existingJob == null) {
            Job newJob = Job.builder()
                    .id(jobId)
                    .title(title)
                    .description(description)
                    .build();
            try {
                jobRepository.save(newJob);
            } catch (Exception exp) {

            }
            return resultHandler.success();
        } else {
            return resultHandler.jobExists(jobId);
        }
    }

    public interface ResultHandler<T> {
        T jobExists(String jobId);
        T fatalError();
        T success();
    }
}
