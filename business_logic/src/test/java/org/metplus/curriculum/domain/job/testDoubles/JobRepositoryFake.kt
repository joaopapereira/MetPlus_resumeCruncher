package org.metplus.curriculum.domain.job.testDoubles

import org.metplus.curriculum.domain.job.Job
import org.metplus.curriculum.domain.job.JobRepository

class JobRepositoryFake : JobRepository {

    private val allJobs = HashMap<String, Job>()
    var saveThrowsException: Exception? = null

    override fun save(job: Job): Job {
        if (saveThrowsException != null)
            throw saveThrowsException as Exception

        val jobToSave = job.copy(
                jobId = if(job.jobId.isEmpty()) {
                    allJobs.size.toString()
                } else {
                    job.jobId
                }
        )

        allJobs.put(job.jobId, jobToSave)

        return job
    }

    override fun findById(jobId: String): Job? {
        return allJobs[jobId]
    }
}
