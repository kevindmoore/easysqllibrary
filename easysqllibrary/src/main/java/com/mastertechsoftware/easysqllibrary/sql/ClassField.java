package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Hold a class's field information
 */
public class ClassField {
    protected String fieldName;
    protected Column.COLUMN_TYPE type;
    protected String value;
    protected int column;

    public ClassField() {
    }

    public ClassField(String fieldName, Column.COLUMN_TYPE type, int column) {
        this.fieldName = fieldName;
        this.type = type;
        this.column = column;
    }

    public ClassField(String fieldName, Column.COLUMN_TYPE type, String value) {
        this.fieldName = fieldName;
        this.type = type;
        this.value = value;
    }

    public ClassField(String fieldName, Column.COLUMN_TYPE type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Column.COLUMN_TYPE getType() {
        return type;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
