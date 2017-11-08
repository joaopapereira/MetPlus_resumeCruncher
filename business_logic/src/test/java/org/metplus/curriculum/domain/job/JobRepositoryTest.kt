package org.metplus.curriculum.domain.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.metplus.curriculum.domain.DomainFactories

abstract class JobRepositoryTest {

    private lateinit var repo: JobRepository

    abstract val repository: JobRepository

    @Before
    fun setup() {
        repo = repository
    }

    @Test
    fun `findById it retrieves the job when job exists`() {
        val job = repo.save(DomainFactories.buildJob())
        val savedJob = repo.findById(job.jobId)
        assertThat(savedJob).isEqualToIgnoringGivenFields(
                job,
                "jobId"
        )
        assertThat(savedJob!!.jobId).isNotEmpty()
    }
}