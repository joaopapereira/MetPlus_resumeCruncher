package org.metplus.curriculum.domain

import org.metplus.curriculum.domain.job.Job

object DomainFactories {
    fun buildJob(): Job {
        return Job(
                jobId = "",
                title = "some title",
                description = "some description",
                starRating = 0.0
        )
    }
}
