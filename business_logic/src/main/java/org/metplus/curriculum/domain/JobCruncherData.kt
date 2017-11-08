package org.metplus.curriculum.domain

data class JobCruncherData(
    var cruncherData: Map<String, JobCruncherDataMap> = emptyMap()
)
