package org.metplus.curriculum.domain.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.metplus.curriculum.domain.DomainFactories.buildJob

abstract class JobRepositoryTest {

    private lateinit var repo: JobRepository

    abstract val repository: JobRepository

    @Before
    fun setup() {
        repo = repository
    }

    @Test
    fun `findById it retrieves the job when job exists`() {
        val job = repo.save(buildJob(
            jobId = "some id"
        ))
        val savedJob = repo.findById(job.jobId)
        assertThat(savedJob).isEqualToIgnoringGivenFields(
            job,
            "jobId"
        )
        assertThat(savedJob!!.jobId).isNotEmpty()
    }

    @Test
    fun `save with existing jobId, it updates the job`() {
        repo.save(buildJob(jobId = "some job id"))
        val savedJob = repo.save(buildJob(
            jobId = "some job id",
            title = "some other title",
            description = "some other title"
        ))

        assertThat(savedJob).isEqualToComparingFieldByField(buildJob(
            jobId = "some job id",
            title = "some other title",
            description = "some other title"
        ))
    }
}