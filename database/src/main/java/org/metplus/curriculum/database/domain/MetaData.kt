package org.metplus.curriculum.database.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.metplus.curriculum.domain.cruncher.CruncherMetaData
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*


/**
 * Created by joao on 3/16/16.
 * This class represents the meta data of a resume
 */
typealias MetaDataComparator = Comparator<Map.Entry<String, MetaDataField<*>>>
typealias OrderedMetaData = List<Map.Entry<String, MetaDataField<*>>>
@Document
open class MetaData(
    @Field var fields: MutableMap<String, MetaDataField<*>> = mutableMapOf()
) : AbstractDocument(), CruncherMetaData {

    @JsonIgnore
    private val orderedFields: List<String>? = null

    fun getOrderedFields(comparator: MetaDataComparator): OrderedMetaData {
        val fieldsData = fields.entries
        if (fieldsData.size == 0)
            return ArrayList()
        val result = ArrayList<Map.Entry<String, MetaDataField<*>>>(fieldsData)
        Collections.sort<Map.Entry<String, MetaDataField<*>>>(result, comparator)
        return result
    }

    /**
     * Retrieve a specific field
     * @param fieldName Name of the field to retrieve
     * @return Null if there are no fields or the field do not exists or the value if exists
     */
    fun getField(fieldName: String): MetaDataField<*>? {
        return if (fields.containsKey(fieldName)) fields[fieldName] else null
    }

    /**
     * Add new meta data fields
     * @param name Name of the field
     * @param data Data of the field
     */
    fun addField(name: String, data: MetaDataField<*>) {
        fields.put(name, data)
    }

    override fun toString(): String {
        var result = "MetaData: "
        if (fields.isNotEmpty())
            for ((key, value) in fields)
                result += "'$key': '$value',"
        else
            result += "empty"
        return result
    }
}
