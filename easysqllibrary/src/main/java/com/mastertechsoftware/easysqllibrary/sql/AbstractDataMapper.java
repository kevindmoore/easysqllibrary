package com.mastertechsoftware.easysqllibrary.sql;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.List;
/**
 *  Class that provides helper method so each class doesn't have to Reproduce
 */
public abstract class AbstractDataMapper<T> implements DataMapper<T> {

	protected int getColumnIndex(Cursor cursor, String column) {
		return cursor.getColumnIndex(column);
	}

    public static boolean isValidType(Class type) {
        if (type.equals(String.class) || type.equals(Number.class) ||
			type.equals(Float.class) || type.equals(Integer.class) ||
            type.equals(Boolean.class) || type.equals(int.class)
            || type.equals(boolean.class) || type.equals(char.class)
            || type.equals(float.class) || type.equals(long.class)
            || type.equals(double.class)) {
            return true;
        }
        return false;
    }

    /**
     * Return the field with the given column name
     * @param allFields
     * @param column
     * @return Field
     */
    public static Field getColumnField(List<Field> allFields, Column column) {
        for (Field allField : allFields) {
            if (allField.getName().equalsIgnoreCase(column.getName())) {
                return allField;
            }
        }
        return null;
    }
}
