package org.metplus.curriculum.domain.job

import org.metplus.curriculum.domain.cruncher.CrunchersList

class CrunchJob(
    private val jobRepository: JobRepository,
    private val crunchersList: CrunchersList
) {
    fun crunch(job: Job) {
        val jobToProcess = job.copy()

        val titleData = mutableMapOf<String, JobCruncherDataMap>()
        val descriptionData = mutableMapOf<String, JobCruncherDataMap>()
        for (cruncher in crunchersList.crunchers) {
            try {
                titleData.put(
                    cruncher.cruncherName,
                    cruncher.crunch(jobToProcess.title) as JobCruncherDataMap
                )
            } catch (exception: Exception) {
                titleData.put(
                    cruncher.cruncherName,
                    JobCruncherDataMap()
                )
            }

            try {
                descriptionData.put(
                    cruncher.cruncherName,
                    cruncher.crunch(jobToProcess.description) as JobCruncherDataMap
                )
            } catch (exception: Exception) {
                descriptionData.put(
                    cruncher.cruncherName,
                    JobCruncherDataMap()
                )
            }
        }
        jobToProcess.titleMetaData = JobCruncherData(titleData)

        jobToProcess.descriptionMetaData = JobCruncherData(descriptionData)
        jobRepository.save(jobToProcess)
    }
}