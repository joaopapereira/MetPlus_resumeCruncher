package org.metplus.curriculum.domain

import org.metplus.curriculum.domain.job.Job
import org.metplus.curriculum.domain.job.JobCruncherData

@JvmOverloads
fun buildJob(
    jobId: String = "",
    title: String = "some title",
    description: String = "some description",
    starRating: Double = 0.0,
    titleMetaData: JobCruncherData? = null,
    descriptiopnMetaData: JobCruncherData? = null
): Job {
    return Job(
        jobId = jobId,
        title = title,
        description = description,
        starRating = starRating,
        titleMetaData = titleMetaData,
        descriptionMetaData = descriptiopnMetaData
    )
}

