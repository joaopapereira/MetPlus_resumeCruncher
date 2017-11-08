package org.metplus.curriculum.domain

data class JobCruncherDataMap(
    val metaData: Map<String, JobCruncherDataField<*>> = emptyMap()
)
