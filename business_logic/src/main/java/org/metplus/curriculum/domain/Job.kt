package org.metplus.curriculum.domain

data class Job (
    var title: String,
    var jobId: String,
    var description: String,
    var titleMetaData: JobCruncherData? = null,
    var descriptionMetaData: JobCruncherData? = null,
    var starRating: Double = 0.toDouble()
) {

    class JobBuilder {
        internal var job = Job(
                jobId = "",
                title = "",
                description = ""
        )

        fun build(): Job {
            return job
        }

        fun title(title: String): JobBuilder {
            job.title = title
            return this
        }

        fun id(id: String): JobBuilder {
            job.jobId = id
            return this
        }

        fun description(description: String): JobBuilder {
            job.description = description
            return this
        }

        fun starRating(starRating: Int): JobBuilder {
            job.starRating = starRating.toDouble()
            return this
        }
    }

    companion object {

        fun builder(): JobBuilder {
            return JobBuilder()
        }
    }
}


