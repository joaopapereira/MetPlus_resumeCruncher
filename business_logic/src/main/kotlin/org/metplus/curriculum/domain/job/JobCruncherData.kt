package org.metplus.curriculum.domain.job

data class JobCruncherData(
    var cruncherData: Map<String, JobCruncherDataMap> = emptyMap()
)
