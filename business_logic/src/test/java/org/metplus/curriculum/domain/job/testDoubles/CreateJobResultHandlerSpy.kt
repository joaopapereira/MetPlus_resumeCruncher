package org.metplus.curriculum.domain.job.testDoubles

import org.metplus.curriculum.domain.job.CreateJob

class CreateJobResultHandlerSpy : CreateJob.ResultHandler<Void?> {
    var jobExistsWasCalledWith: String? = null
    var successWasCalled = false
    var fatalErrorWasCalledWith: String? = null

    override fun jobExists(jobId: String): Void? {
        jobExistsWasCalledWith = jobId
        return null
    }

    override fun fatalError(jobId: String): Void? {
        fatalErrorWasCalledWith = jobId
        return null
    }

    override fun success(): Void? {
        successWasCalled = true
        return null
    }
}
