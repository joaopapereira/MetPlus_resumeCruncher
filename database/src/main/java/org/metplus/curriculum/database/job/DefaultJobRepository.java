package org.metplus.curriculum.database.job;

import org.metplus.curriculum.database.domain.JobMongo;
import org.metplus.curriculum.database.repository.JobDocumentRepository;
import org.metplus.curriculum.domain.Job;
import org.metplus.curriculum.domain.job.JobRepository;
import org.springframework.stereotype.Component;

@Component
public class DefaultJobRepository implements JobRepository {
    private final JobDocumentRepository repository;

    public DefaultJobRepository(JobDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Job save(Job job) {
        return repository.save(new JobMongo(job)).toJob();
    }

    @Override
    public Job findById(String jobId) {
        return repository.findByJobId(jobId).toJob();
    }
}
