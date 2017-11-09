package org.metplus.curriculum.domain.job

data class JobCruncherDataMap(
    val metaData: Map<String, JobCruncherDataField<*>> = emptyMap()
)
