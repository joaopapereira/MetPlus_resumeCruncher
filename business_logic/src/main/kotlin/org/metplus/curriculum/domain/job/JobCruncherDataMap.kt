package org.metplus.curriculum.domain.job

import org.metplus.curriculum.domain.cruncher.CruncherMetaData

data class JobCruncherDataMap(
    var metaData: Map<String, JobCruncherDataField<*>> = emptyMap()
) : CruncherMetaData
