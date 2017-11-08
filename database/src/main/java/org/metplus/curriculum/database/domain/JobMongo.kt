package org.metplus.curriculum.database.domain

import org.metplus.curriculum.domain.Job
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import javax.print.Doc

/**
 * JobMongo Model
 */
@Document
data class JobMongo(
        @Field val jobId: String,
        @Field val title: String,
        @Field val description: String,
        @Field val titleMetaData: DocumentWithMetaData? = null,
        @Field val descriptionMetaData: DocumentWithMetaData? = null,
        @Transient val starRating: Double = 0.0
) : DocumentWithMetaData() {
    /**
     * Check if the JobMongo have data from a specific cruncher
     * @param cruncherName Name of the cruncher
     * @return True if have data, False otherwise
     */
    fun haveCruncherData(cruncherName: String): Boolean {
        return getTitleCruncherData(cruncherName) != null && getTitleCruncherData(cruncherName)!!.fields.size > 0 || getDescriptionCruncherData(cruncherName) != null && getDescriptionCruncherData(cruncherName)!!.fields.size > 0
    }

    /**
     * Retrieve the Meta data from crunching the title
     * @param cruncherName Cruncher name
     * @return Meta data
     */
    fun getTitleCruncherData(cruncherName: String): MetaData? {
        return if (titleMetaData == null || titleMetaData!!.metaData == null) null else titleMetaData!!.metaData[cruncherName]
    }

    /**
     * Retrieve all the crunchers meta data from the title
     * @return Map with all crunchers meta data
     */
    val titleCruncherData: Map<String, MetaData>?
        get() = if (titleMetaData == null) null else titleMetaData!!.metaData

    /**
     * Retrieve the Meta Data from crunching the description
     * @param cruncherName Cruncher name
     * @return Meta data
     */
    fun getDescriptionCruncherData(cruncherName: String): MetaData? {
        return if (descriptionMetaData == null || descriptionMetaData!!.metaData == null) null else descriptionMetaData!!.metaData[cruncherName]
    }

    /**
     * Retrieve all the crunchers meta data from the description
     * @return Map with all crunchers meta data
     */
    val descriptionCruncherData: Map<String, MetaData>?
        get() = if (descriptionMetaData == null) null else descriptionMetaData!!.metaData
}
