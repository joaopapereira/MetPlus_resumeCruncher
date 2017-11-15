package org.metplus.curriculum.domain.job

class UpdateJob(
    private val jobRepository: JobRepository
) {
    fun <T> execute(jobId: String, title: String?, description: String?, resultHandler: ResultHandler<T>): T {
        val job = jobRepository.findById(jobId = jobId) ?: return resultHandler.doesNotExist(jobId)
        if (title != null)
            job.title = title
        if (description != null)
            job.description = description
        try {
            jobRepository.save(job)
        } catch (exception: Exception){
            return resultHandler.fatalError(job, exception)
        }
        return resultHandler.success(job)
    }

    interface ResultHandler<T> {
        fun doesNotExist(jobId: String): T
        fun success(job: Job): T
        fun fatalError(job: Job, exception: Exception): T
    }
}