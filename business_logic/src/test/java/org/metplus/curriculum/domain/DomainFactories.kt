package org.metplus.curriculum.domain

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
