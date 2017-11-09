package org.metplus.curriculum.domain.job;

import org.metplus.curriculum.domain.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobRepositoryFake implements JobRepository {

    private Map<String, Job> allJobs = new HashMap<>();
    public Exception saveThrowsException = null;

    @Override
    public Job save(Job job) {
        if(saveThrowsException != null)
            throw saveThrowsException;
        if(job.getJobId() == null || job.getJobId().length() == 0) {
            job.setJobId("" + allJobs.size());
            allJobs.put(job.getJobId(), job);
        } else {
            allJobs.put(job.getJobId(), job);
        }
        return job;
    }

    @Override
    public Job findById(String jobId) {
        return allJobs.get(jobId);
    }
}
