package org.metplus.curriculum.domain.job;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.metplus.curriculum.domain.DomainFactories;
import org.metplus.curriculum.domain.Job;

public abstract class JobRepositoryTest {

    private JobRepository repo;

    protected abstract JobRepository getRepository();

    @Before
    public void setup() {
        repo = getRepository();
    }

    @Test
    public void findById_it_retrieves_the_job_when_job_exists() {
        Job job = repo.save(DomainFactories.buildJob());
        Job savedJob = repo.findById(job.getJobId());
        Assertions.assertThat(savedJob).isEqualToComparingFieldByField(job);
    }
}