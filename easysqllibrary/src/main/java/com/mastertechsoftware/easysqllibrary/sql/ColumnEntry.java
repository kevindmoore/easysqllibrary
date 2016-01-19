package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Starter Column Entry. Each table has a set of string/int values for
 * Column name and column index
 */
public class ColumnEntry {
    protected String fieldName;
    protected int columnNumber;

    public ColumnEntry(String field, int column) {
        fieldName = field;
        columnNumber = column;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}
