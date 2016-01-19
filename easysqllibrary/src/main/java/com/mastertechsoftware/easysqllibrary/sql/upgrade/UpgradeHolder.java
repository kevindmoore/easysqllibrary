package com.mastertechsoftware.easysqllibrary.sql.upgrade;

import com.mastertechsoftware.easysqllibrary.sql.ClassField;
import com.mastertechsoftware.easysqllibrary.sql.Column;

import java.util.ArrayList;
import java.util.List;
/**
 * Hold a list of generic field info. field name, type and value
 */
public class UpgradeHolder {
    protected List<ClassField> fields = new ArrayList<ClassField>();

    public void addField(String fieldName, Column.COLUMN_TYPE type, int column) {
        addField(new ClassField(fieldName, type, column));
    }
    public void addField(String fieldName, Column.COLUMN_TYPE type, String value) {
        addField(new ClassField(fieldName, type, value));
    }

    public void addField(String fieldName, Column.COLUMN_TYPE type) {
        addField(new ClassField(fieldName, type));
    }

    public void addField(ClassField classField) {
        fields.add(classField);
    }

    public void removeField(ClassField classField) {
        fields.remove(classField);
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public ClassField getField(int position) {
        if (fields.size() <= position) {
            return null;
        }
        return fields.get(position);
    }

    public ClassField getField(String fieldName) {
        for (ClassField field : fields) {
            if (fieldName.equalsIgnoreCase(field.getFieldName())) {
                return field;
            }
        }
        return null;
    }
}
