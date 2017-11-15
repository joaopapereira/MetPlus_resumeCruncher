package org.metplus.curriculum.domain.job.testDoubles

import org.metplus.curriculum.domain.job.CreateJob
import org.metplus.curriculum.domain.job.Job

class CreateJobResultHandlerSpy : CreateJob.ResultHandler<Void?> {
    var jobExistsWasCalledWith: String? = null
    var successWasCalledWith: Job? = null
    var fatalErrorWasCalledWith: String? = null

    override fun jobExists(jobId: String): Void? {
        jobExistsWasCalledWith = jobId
        return null
    }

    override fun fatalError(jobId: String): Void? {
        fatalErrorWasCalledWith = jobId
        return null
    }

    override fun success(job: Job): Void? {
        successWasCalledWith = job
        return null
    }
}
