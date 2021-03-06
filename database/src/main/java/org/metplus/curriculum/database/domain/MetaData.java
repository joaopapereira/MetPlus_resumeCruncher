package org.metplus.curriculum.database.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;

/**
 * Created by joao on 3/16/16.
 * This class represents the meta data of a resume
 */
@Document
public class MetaData extends AbstractDocument implements CruncherMetaData {
    @Field
    private Map<String, MetaDataField> fields = new HashMap<>();

    @JsonIgnore
    private List<String> orderedFields;

    /**
     * Retrieve all the fields meta data
     * @return Map with all the fields
     */
    public Map<String, MetaDataField> getFields() {
        return fields;
    }

    public List<Map.Entry<String, MetaDataField>> getOrderedFields(Comparator<Map.Entry<String, MetaDataField>> comparator) {
        if(fields == null)
            return new ArrayList<>();
        Set<Map.Entry<String, MetaDataField>> fieldsData = fields.entrySet();
        if(fieldsData == null || fieldsData.size() == 0)
            return new ArrayList<>();
        List<Map.Entry<String, MetaDataField>> result = new ArrayList<>(fieldsData);
        Collections.sort(result, comparator);
        return result;
    }

    /**
     * Retrieve a specific field
     * @param fieldName Name of the field to retrieve
     * @return Null if there are no fields or the field do not exists or the value if exists
     */
    public MetaDataField getField(String fieldName) {
        if(fields == null)
            return null;
        if(fields.containsKey(fieldName))
            return fields.get(fieldName);
        return null;
    }

    /**
     * Add new meta data fields
     * @param name Name of the field
     * @param data Data of the field
     */
    public void addField(String name, MetaDataField data) {
        if(fields == null)
            fields = new HashMap<>();
        fields.put(name, data);
    }

    /**
     * Set the Meta data fields
     * @param fields Meta data fields
     */
    public void setFields(Map<String, MetaDataField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        String result = "MetaData: ";
        if(fields != null)
            for(Map.Entry entry: fields.entrySet())
                result += "'" + entry.getKey() + "': '" + entry.getValue()+ "',";
        else
            result += "empty";
        return result;
    }
}
