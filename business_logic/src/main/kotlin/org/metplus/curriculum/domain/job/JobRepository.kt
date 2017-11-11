package org.metplus.curriculum.domain.job

interface JobRepository {
    fun save(job: Job): Job
    fun findById(jobId: String): Job?
}
