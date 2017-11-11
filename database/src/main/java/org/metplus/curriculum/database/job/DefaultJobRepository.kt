package org.metplus.curriculum.database.job

import org.metplus.curriculum.database.domain.DocumentWithMetaData
import org.metplus.curriculum.database.domain.JobMongo
import org.metplus.curriculum.database.domain.MetaData
import org.metplus.curriculum.database.domain.MetaDataField
import org.metplus.curriculum.database.repository.JobDocumentRepository
import org.metplus.curriculum.domain.job.*
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
class DefaultJobRepository(private val repository: JobDocumentRepository) : JobRepository {

    override fun save(job: Job): Job {
        return repository.save(job.toJobMongo()).toJob()
    }

    override fun findById(jobId: String): Job? {
        return repository.findByJobId(jobId).toJob()
    }
}

private fun JobMongo.toJob(): Job =
        Job(
                jobId = jobId,
                title = title,
                description = description,
                starRating = starRating,
                descriptionMetaData = descriptionMetaData?.toJobCruncherData(),
                titleMetaData = titleMetaData?.toJobCruncherData()
        )

private fun DocumentWithMetaData.toJobCruncherData(): JobCruncherData? {
    val metaData: MutableMap<String, JobCruncherDataMap> = mutableMapOf()
    this.metaData.forEach({metaData.put(it.key, it.value.toJobCruncherMetaData())})
    return JobCruncherData(
        cruncherData = metaData
    )
}

private fun MetaData.toJobCruncherMetaData(): JobCruncherDataMap {
    val metaData: MutableMap<String, JobCruncherDataField<*>> = mutableMapOf()
    this.fields.forEach {metaData.put(it.key, it.value.toJobCruncherDataField())}
    return JobCruncherDataMap(
        metaData = metaData
    )
}

private fun <T> MetaDataField<T>.toJobCruncherDataField(): JobCruncherDataField<T> {
    return JobCruncherDataField(data)
}

private fun Job.toJobMongo(): JobMongo {
    return JobMongo(
            jobId = jobId,
            title = title,
            description = description,
            starRating = starRating,
            descriptionMetaData = descriptionMetaData?.toDocumentWithMetaData(),
            titleMetaData = titleMetaData?.toDocumentWithMetaData()
    )
}

private fun JobCruncherData.toDocumentWithMetaData(): DocumentWithMetaData? {
    val metaData: MutableMap<String, MetaData> = mutableMapOf()
    this.cruncherData.forEach {metaData.put(it.key, it.value.toMetaData())}
    return DocumentWithMetaData(
            metaData = metaData
    )
}

private fun JobCruncherDataMap.toMetaData(): MetaData {
    val metaData: MutableMap<String, MetaDataField<*>> = mutableMapOf()
    this.metaData.forEach {metaData.put(it.key, it.value.toMetaDataField())}
    return MetaData(
            fields = metaData
    )
}

private fun <T> JobCruncherDataField<T>.toMetaDataField(): MetaDataField<T> {
    return MetaDataField(this.value)
}
