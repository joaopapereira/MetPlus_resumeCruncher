package org.metplus.curriculum.database.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.mongodb.core.mapping.Field

/**
 * Class that can be extended when we need a document
 * with meta data
 */
open class DocumentWithMetaData(
        @Field var metaData: Map<String, MetaData> = mapOf()
) : AbstractDocument() {
    /**
     * Check if a cruncher already have processed this resume
     * @param cruncherName Name of the cruncher
     * @return True if meta data is present, false otherwise
     */
    @JsonIgnore
    fun isCruncherDataAvailable(cruncherName: String): Boolean {
        return metaData.containsKey(cruncherName)
    }

    /**
     * Retrieve meta data from a specific cruncher
     * @param cruncherName Name of the cruncher
     * @return Cruncher meta data
     */
    fun getCruncherData(cruncherName: String): MetaData? {
        return metaData[cruncherName]
    }
}
