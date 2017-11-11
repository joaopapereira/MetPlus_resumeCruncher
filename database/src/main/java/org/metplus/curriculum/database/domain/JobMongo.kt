package org.metplus.curriculum.database.domain

import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * JobMongo Model
 */
@Document
data class JobMongo @JvmOverloads constructor (
        @Field val jobId: String = "",
        @Field val title: String = "",
        @Field val description: String = "",
        @Field val titleMetaData: DocumentWithMetaData? = null,
        @Field val descriptionMetaData: DocumentWithMetaData? = null,
        @Transient val starRating: Double = 0.0
) : AbstractDocument() {
    /**
     * Check if the JobMongo have data from a specific cruncher
     * @param cruncherName Name of the cruncher
     * @return True if have data, False otherwise
     */
    fun haveCruncherData(cruncherName: String): Boolean {
        return getTitleCruncherData(cruncherName) != null &&
                getTitleCruncherData(cruncherName)!!.fields.isNotEmpty() ||
                getDescriptionCruncherData(cruncherName) != null &&
                getDescriptionCruncherData(cruncherName)!!.fields.isNotEmpty()
    }

    /**
     * Retrieve the Meta data from crunching the title
     * @param cruncherName Cruncher name
     * @return Meta data
     */
    fun getTitleCruncherData(cruncherName: String): MetaData? {
        return if (titleMetaData == null) null else titleMetaData.metaData[cruncherName]
    }

    /**
     * Retrieve the Meta Data from crunching the description
     * @param cruncherName Cruncher name
     * @return Meta data
     */
    fun getDescriptionCruncherData(cruncherName: String): MetaData? {
        return if (descriptionMetaData == null) null else descriptionMetaData.metaData[cruncherName]
    }
}
