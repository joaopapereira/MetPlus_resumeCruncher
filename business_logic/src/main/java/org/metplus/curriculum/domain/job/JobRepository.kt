package org.metplus.curriculum.domain.job

import org.metplus.curriculum.domain.Job

interface JobRepository {
    fun save(job: Job): Job
    fun findById(jobId: String): Job?
}
