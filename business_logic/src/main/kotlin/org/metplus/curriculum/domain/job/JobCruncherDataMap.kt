package org.metplus.curriculum.domain.job

data class JobCruncherDataMap(
    var metaData: Map<String, JobCruncherDataField<*>> = emptyMap()
)
