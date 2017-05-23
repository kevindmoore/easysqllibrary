package com.mastertechsoftware.easysqllibrary.sql.upgrade;

import com.mastertechsoftware.easysqllibrary.sql.AbstractDataMapper;
import com.mastertechsoftware.easysqllibrary.sql.AbstractTable;
import com.mastertechsoftware.easysqllibrary.sql.ClassField;
import com.mastertechsoftware.easysqllibrary.sql.Column;
import com.mastertechsoftware.easysqllibrary.sql.Database;
import com.mastertechsoftware.logging.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;
/**
 * Table used to hold old data from the database
 */
public class UpgradeTable extends AbstractTable<UpgradeHolder> {
    protected Mapper mapper = new Mapper();
    protected List<? extends UpgradeHolder> allEntries;
    protected List<ClassField> fields = new ArrayList<ClassField>();

    public List<? extends UpgradeHolder> getAllEntries() {
        return allEntries;
    }

    public void setAllEntries(List<? extends UpgradeHolder> allEntries) {
        this.allEntries = allEntries;
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public void setFields(List<ClassField> fields) {
        this.fields = fields;
    }

    public void addField(String fieldName, Column.COLUMN_TYPE type, int column) {
        addField(new ClassField(fieldName, type, column));
    }

    public void addField(String fieldName, Column.COLUMN_TYPE type) {
        addField(new ClassField(fieldName, type));
    }

    public void addField(ClassField classField) {
        fields.add(classField);
        addColumn(new Column(classField.getFieldName(), classField.getType()));
    }

    /**
     * Insert entries from the allEntries array
     * @param database
     */
    public void insertEntries(Database database) {
        if (allEntries == null || allEntries.size() == 0) {
            return;
        }
        ContentValues cv = new ContentValues();
        for (UpgradeHolder allEntry : allEntries) {
            for (int i = 0; i < allEntry.fields.size(); i++) {
                final ClassField field = allEntry.getField(i);
                cv.put(field.getFieldName(), field.getValue());
            }
            try {
                final long row = database.getDatabase().insert(getTableName(), null, cv);
                if (row < 0) {
                    Logger.error(this, "Could not insert row for " + getTableName() + " values of " + cv );
                }
            } catch (SQLiteException e) {
                Logger.error(this, e.getMessage());
            }
            cv.clear();
        }
    }

    public Mapper getMapper() {
        return mapper;
    }

    /**
     * Mapper class. Get all fields and use the column to get it's type
     */
    public class Mapper extends AbstractDataMapper<UpgradeHolder> {

        @Override
        public void write(ContentValues cv, Column column, UpgradeHolder upgradeHolder) {
            if (fields.size() <= column.getColumnPosition()) {
                Logger.error(this, "Field at position " + column.getColumnPosition() + " does not exist");
                return;
            }
            ClassField field = fields.get(column.getColumnPosition());

            ClassField holderField = upgradeHolder.getField(column.getColumnPosition());
            if (holderField == null) {
                upgradeHolder.addField(field.getFieldName(), field.getType(), column.getColumnPosition());
                holderField = upgradeHolder.getField(column.getColumnPosition());
            }

            // Need to skip ID
            if (column.getName().equalsIgnoreCase(ID)) {
                return;
            }
            switch (column.getType()) {
                case TEXT:
                    cv.put(column.getName(), holderField.getValue());
                    break;
                case INTEGER:
                    cv.put(column.getName(), Integer.valueOf(holderField.getValue()));
                    break;
                case FLOAT:
                    cv.put(column.getName(), Float.valueOf(holderField.getValue()));
                    break;
                case BOOLEAN:
                    cv.put(column.getName(), Boolean.valueOf(holderField.getValue()));
                    break;
                case LONG:
                    cv.put(column.getName(), Long.valueOf(holderField.getValue()));
                    break;
                case DOUBLE:
                    cv.put(column.getName(), Double.valueOf(holderField.getValue()));
                    break;
            }
        }

        @Override
        public void read(Cursor cursor, Column column, UpgradeHolder upgradeHolder) {
            int columnIndex = getColumnIndex(cursor, column.getName());
            if (columnIndex == -1) {
                Logger.error(this, "Mapper.read: Column " + column.getName() + " does not exist in cursor");
                return;
            }
            if (fields.size() <= column.getColumnPosition()) {
                Logger.error(this, "Field at position " + column.getColumnPosition() + " does not exist");
                return;
            }
            ClassField field = fields.get(columnIndex);
            ClassField holderField = upgradeHolder.getField(columnIndex);
            if (holderField == null) {
                upgradeHolder.addField(field.getFieldName(), field.getType(), columnIndex);
                holderField = upgradeHolder.getField(column.getColumnPosition());
            }

            holderField.setValue(cursor.getString(columnIndex));
        }
    }
}
