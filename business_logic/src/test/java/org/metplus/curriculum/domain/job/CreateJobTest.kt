package org.metplus.curriculum.domain.job

import org.assertj.core.api.Assertions.assertThat

import org.junit.Before
import org.junit.Test
import org.metplus.curriculum.domain.DomainFactories
import org.metplus.curriculum.domain.job.testDoubles.CreateJobResultHandlerSpy
import org.metplus.curriculum.domain.job.testDoubles.JobRepositoryFake

class CreateJobTest {
    private lateinit var jobRepositoryFake: JobRepositoryFake
    private lateinit var createJob: CreateJob
    @Before
    fun setup() {
        jobRepositoryFake = JobRepositoryFake()
        createJob = CreateJob(jobRepositoryFake)
    }

    @Test
    fun `when jobId exists, it calls job exists callback`() {
        var job = DomainFactories.buildJob()
        job = jobRepositoryFake.save(job)
        val resultHandler = CreateJobResultHandlerSpy()
        val title = ""
        val description = ""
        createJob.create(
                jobId = job.jobId,
                title = title,
                description = description,
                resultHandler = resultHandler)
        assertThat(resultHandler.jobExistsWasCalledWith).isEqualTo(job.jobId)
    }

    @Test
    fun `when error happens while saving, it calls fatal error callback`() {
        jobRepositoryFake.saveThrowsException = Exception()
        val resultHandler = CreateJobResultHandlerSpy()
        val title = ""
        val description = ""
        createJob.create(
                jobId = "asd",
                title = title,
                description = description,
                resultHandler = resultHandler
        )
        assertThat(resultHandler.fatalErrorWasCalledWith).isEqualTo("asd")
    }

    @Test
    fun `when jobId do not exist, it saves in database and call success callback`() {
        val job = DomainFactories.buildJob()
        job.jobId = "someJobId"
        val resultHandler = CreateJobResultHandlerSpy()
        createJob.create(
                jobId = job.jobId,
                title = job.title,
                description = job.description,
                resultHandler = resultHandler
        )

        val createdJob = jobRepositoryFake.findById("someJobId")
        assertThat(job).isEqualToComparingFieldByField(createdJob)

        assertThat(resultHandler.successWasCalled).isEqualTo(true)
    }
}