package org.metplus.curriculum.domain.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.metplus.curriculum.domain.job.testDoubles.JobRepositoryFake

class UpdateJobTest {
    private lateinit var jobRepositoryFake: JobRepositoryFake
    private lateinit var updateJob: UpdateJob
    @Before
    fun `set up`() {
        jobRepositoryFake = JobRepositoryFake()
        updateJob = UpdateJob(
            jobRepository = jobRepositoryFake
        )
    }

    @Test
    fun `when update a job that does not exist, calls job not exists callback`() {
        val jobId = "some id"
        val title = "new title"
        val description = "new description"
        val resultHandlerSpy = ResultHandlerSpy()
        updateJob.execute(jobId, title, description, resultHandlerSpy)
        assertThat(resultHandlerSpy.doesNotExistWasCalledWith).isEqualTo(jobId)
    }

    @Test
    fun `when title is null, do not update the title`() {
        val resultHandlerSpy = ResultHandlerSpy()
        val job = Job(
            jobId = "some id",
            title = "some title",
            description = "some description"
        )
        jobRepositoryFake.save(job)
        updateJob.execute(
            jobId = "some id",
            title = null,
            description = "some other description",
            resultHandler = resultHandlerSpy
        )

        val savedJob = jobRepositoryFake.findById(jobId = "some id")
        assertThat(savedJob).isEqualToComparingFieldByField(Job(
            jobId = "some id",
            title = "some title",
            description = "some other description"
        ))
        assertThat(resultHandlerSpy.successWasCalled).isEqualTo(true)
    }

    @Test
    fun `when description is null, do not update the description`() {
        val resultHandlerSpy = ResultHandlerSpy()
        val job = Job(
            jobId = "some id",
            title = "some title",
            description = "some description"
        )
        jobRepositoryFake.save(job)
        updateJob.execute(
            jobId = "some id",
            title = "some other title",
            description = null,
            resultHandler = resultHandlerSpy
        )

        val savedJob = jobRepositoryFake.findById(jobId = "some id")
        assertThat(savedJob).isEqualToComparingFieldByField(Job(
            jobId = "some id",
            title = "some other title",
            description = "some description"
        ))
        assertThat(resultHandlerSpy.successWasCalled).isEqualTo(true)
    }

    @Test
    fun `when job exists, it updates it`() {
        val resultHandlerSpy = ResultHandlerSpy()
        val job = Job(
            jobId = "some id",
            title = "some title",
            description = "some description"
        )
        jobRepositoryFake.save(job)
        updateJob.execute(
            jobId = "some id",
            title = "some other title",
            description = "some other description",
            resultHandler = resultHandlerSpy
        )

        val savedJob = jobRepositoryFake.findById(jobId = "some id")
        assertThat(savedJob).isEqualToComparingFieldByField(Job(
            jobId = "some id",
            title = "some other title",
            description = "some other description"
        ))
        assertThat(resultHandlerSpy.successWasCalled).isEqualTo(true)
    }

    @Test
    fun `when exception occur while saving, call fatal error callback`() {
        val resultHandlerSpy = ResultHandlerSpy()
        val job = Job(
            jobId = "some id",
            title = "some title",
            description = "some description"
        )
        jobRepositoryFake.save(job)

        jobRepositoryFake.saveThrowsException = Exception()
        updateJob.execute(
            jobId = "some id",
            title = "some other title",
            description = "some other description",
            resultHandler = resultHandlerSpy
        )

        assertThat(resultHandlerSpy.fatalErrorWasCalled).isEqualTo(true)
    }
}

class ResultHandlerSpy : UpdateJob.ResultHandler<Unit> {
    var doesNotExistWasCalledWith: String? = null

    var successWasCalled = false

    var fatalErrorWasCalled = false

    override fun doesNotExist(jobId: String) {
        doesNotExistWasCalledWith = jobId
    }

    override fun success() {
        successWasCalled = true
    }

    override fun fatalError(job: Job, exception: Exception) {
        fatalErrorWasCalled = true
    }
}
