package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Hold a column name and value. Used for where searches
 * Column name and column value
 */
public class ColumnValue {
    protected String fieldName;
    protected String value;

    public ColumnValue(String field, String column) {
        fieldName = field;
        value = column;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }
}
