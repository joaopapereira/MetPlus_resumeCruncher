package org.metplus.curriculum.domain.job;

import org.metplus.curriculum.domain.Job;

public interface JobRepository {
    Job save(Job job);
    Job findById(String jobId);
}
