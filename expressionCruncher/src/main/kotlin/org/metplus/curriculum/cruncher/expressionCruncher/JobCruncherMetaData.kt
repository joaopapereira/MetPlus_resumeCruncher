package org.metplus.curriculum.cruncher.expressionCruncher

import org.metplus.curriculum.database.domain.MetaData
import org.metplus.curriculum.database.domain.MetaDataField

/**
 * Created by joao on 3/25/16.
 */
class JobCruncherMetaData : MetaData() {
    val titleData: Map<String, MetaDataField<*>>?
        get() {
            val data:MetaDataField<MetaData> = fields["bamm"] as MetaDataField<MetaData>? ?: return null

            return data.data?.fields
        }

    fun setTitleData(metaData: ExpressionCruncherMetaData) {
        val dataField = MetaDataField<MetaData>(metaData)
        addField("bamm", dataField)
    }
}