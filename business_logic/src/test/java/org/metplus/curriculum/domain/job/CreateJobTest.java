package org.metplus.curriculum.domain.job;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.metplus.curriculum.domain.DomainFactories;
import org.metplus.curriculum.domain.Job;

public class CreateJobTest {
    private JobRepositoryFake jobRepositoryFake;
    private CreateJob<Void> createJob;
    @Before
    public void setup() {
        jobRepositoryFake = new JobRepositoryFake();
        createJob = new CreateJob<>(jobRepositoryFake);
    }

    @Test
    public void when_job_id_exists_it_calls_job_exists_callback() {
        Job job = DomainFactories.buildJob();
        job = jobRepositoryFake.save(job);
        CreateJobResultHandlerSpy resultHandler = new CreateJobResultHandlerSpy();
        String title = "";
        String description = "";
        createJob.create(job.getJobId(), title, description, resultHandler);
        assertThat(resultHandler.jobExistsWasCalledWith).isEqualTo(job.getJobId());
    }

    @Test
    public void when_error_happens_while_saving_it_calls_fatal_error_callback() {
        Job job = DomainFactories.buildJob();
        job = jobRepositoryFake.save(job);
        CreateJobResultHandlerSpy resultHandler = new CreateJobResultHandlerSpy();
        String title = "";
        String description = "";
        createJob.create(job.getJobId(), title, description, resultHandler);
        assertThat(resultHandler.fatalErrorWasCalled).isEqualTo(job.getJobId());
    }

    @Test
    public void when_job_id_do_not_exist_it_saves_in_database_and_call_success_callback() {
        Job job = DomainFactories.buildJob();
        job.setJobId("someJobId");
        CreateJobResultHandlerSpy resultHandler = new CreateJobResultHandlerSpy();
        createJob.create(job.getJobId(), job.getTitle(), job.getDescription(), resultHandler);

        Job createdJob = jobRepositoryFake.findById("someJobId");
        assertThat(job).isEqualToComparingFieldByField(createdJob);

        assertThat(resultHandler.successWasCalled).isEqualTo(true);
    }
}