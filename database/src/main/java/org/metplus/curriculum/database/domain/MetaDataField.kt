package org.metplus.curriculum.database.domain

/**
 * Meta data field information
 */
data class MetaDataField<T>(
        var data: T?) {

    override fun toString(): String {
        var result = "MetaDataField: "
        result += "'$data'"
        return result
    }
}
