package org.metplus.curriculum.domain.job

import org.metplus.curriculum.domain.Job

class CreateJob(private val jobRepository: JobRepository) {

    fun <T> create(jobId: String, title: String, description: String, resultHandler: ResultHandler<T>): T {
        val existingJob = jobRepository.findById(jobId)
        if (existingJob == null) {
            val newJob = Job(
                    jobId = jobId,
                    title = title,
                    description = description
            )
            try {
                jobRepository.save(newJob)
            } catch (exp: Exception) {
                return resultHandler.fatalError(jobId)
            }
            return resultHandler.success()
        } else {
            return resultHandler.jobExists(jobId)
        }
    }

    interface ResultHandler<T> {
        fun jobExists(jobId: String): T
        fun fatalError(jobId: String): T
        fun success(): T
    }
}
