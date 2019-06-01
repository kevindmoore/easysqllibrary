
package com.mastertechsoftware.easysqllibrary.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.mastertechsoftware.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements all methods so subclasses can implement only those methods needed.
 */
public class AbstractTable<T> extends Table<T> {

    public AbstractTable() {
    }

    public AbstractTable(String tableName) {
        super(tableName);
    }

    public AbstractTable(List<Column> columns, String tableName) {
        super(columns, tableName);
    }

    @Override
    public T insertEntry(Database database, T data) {
        return null;
    }

    /**
     * Insert a new entry into the db using a mapper
     * @param database
     * @param data
     * @param mapper
     * @return new id
     */
    public long insertEntry(Database database, T data, DataMapper<T> mapper) throws DBException {
        int columnPosition = 0;
        ContentValues cv = new ContentValues();
        for (Column column : columns) {
            if (column.column_position == 0) {
                column.column_position = columnPosition;
            }
            mapper.write(cv, column, data);
            columnPosition++;
        }
        return insertEntry(database, cv);
    }

    /**
     * Insert a new row with the given column data.
     * @param database
     * @param data
     * @return id of new item
     */
    @Override
    public long insertEntry(Database database, List<String> data) throws DBException {
        ContentValues cv = new ContentValues();
        int columnSize = columns.size();
        int dataSize = data.size();
        if (dataSize > (columnSize-1)) {
            Logger.error(this, "You cannot insert more data than there are columns");
            return 0;
        }
        // Assume the first column is the id column
        for (int i=0; i < dataSize && i < columnSize; i++) {
            cv.put(columns.get(i+1).getName(), data.get(i));
        }
        return insertEntry(database, cv);
    }

    /**
     * Insert a new row with the given column data.
     * @param database
     * @param data
     * @return id of new item
     */
    @Override
    public long insertEntry(Database database, ContentValues data) throws DBException {
        long id = 0;
        try {
            id = database.getDatabase().insert(getTableName(), getIdField(), data);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
        return id;
    }

    /**
     * Delete a single entry with the given id
     * @param database
     * @param key - id to delete
     */
    @Override
    public long deleteEntry(Database database, Object key) throws DBException {
        String[] whereArgs = new String[1];
        whereArgs[0] = String.valueOf(key);
        try {
            return database.getDatabase().delete(getTableName(), getIdField() + "=?", whereArgs);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
    }

    /**
     * Delete an entry with the given where clause. Needs to use "?" formats
     * @param database
     * @param whereClause
     * @param whereArgs
     */
    @Override
    public long deleteEntryWhere(Database database, String whereClause, String[] whereArgs) throws DBException {
        try {
            return database.getDatabase().delete(getTableName(), whereClause, whereArgs);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
    }

	/**
	 * Delete the entry with the given where column and value
	 * @param database
	 * @param columnName
	 * @param columnValue
	 */
    public long deleteEntryWhere(Database database, String columnName, String columnValue) throws DBException {
		String[] whereArgs = new String[1];
		whereArgs[0] = columnValue;
        try {
            return database.getDatabase().delete(getTableName(), columnName + "=?" , whereArgs);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
    }

    /**
     * Delete all entries in this table.
     * @param database
     */
    @Override
    public void deleteAllEntries(Database database) throws DBException {
        try {
            database.getDatabase().delete(getTableName(), null, null);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
    }

    /**
     * Get a single entry and return the cursor.
     * @param database
     * @param key
     * @return Cursor
     */
    @Override
    public T getEntry(Database database, Object key) {
        return null;
    }

    /**
     * Get a single entry and return the object using a mapper.
     * @param database
     * @param key
     * @param mapper
     * @return T
     */
    public T getEntry(Database database, Object key, T data, DataMapper<T> mapper) throws DBException {
        Cursor cursor = null;
        String[] params = { String.valueOf(key) };
        try {
            cursor = database.getDatabase().query(getTableName(), getProjection(), getIdField() + "=?",
                    params, null, null, null);
            if (cursor == null) {
                return null;
            }
            if (!cursor.moveToNext()) {
                cursor.close();
                return null;
            }
            int columnPosition = 0;
            for (Column column : columns) {
                if (column.column_position == 0) {
                    column.column_position = columnPosition;
                }
                mapper.read(cursor, column, data);
                columnPosition++;
            }
            return data;
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Get a single entry that matches the given selection and selection args. This will provide more than just 1 item to search on.
     * @param database
     * @param selection
     * @param selectionArgs
     * @param data
     * @param mapper
     * @return T data
     */
    public T getEntry(Database database, String selection, String[] selectionArgs, T data, DataMapper<T> mapper) throws DBException {
        Cursor cursor = null;
        try {
            cursor = database.getDatabase().query(getTableName(), getProjection(), selection,
                                                  selectionArgs, null, null, null);
            if (cursor == null) {
                return null;
            }
            if (!cursor.moveToNext()) {
                cursor.close();
                return null;
            }
            int columnPosition = 0;
            for (Column column : columns) {
                if (column.column_position == 0) {
                    column.column_position = columnPosition;
                }
                mapper.read(cursor, column, data);
                columnPosition++;
            }
            return data;
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Get a single entry and return the object using a mapper based on the column search.
     * @param database
     * @param data
     * @param columnName
     * @param columnValue
     * @param mapper
     * @return T
     */
    public T getEntry(Database database, T data, String columnName, String columnValue, DataMapper<T> mapper) throws DBException {
        Cursor cursor = null;
        String[] params = { String.valueOf(columnValue) };
        try {
            cursor = database.getDatabase().query(getTableName(), getProjection(), columnName + "=?",
                    params, null, null, null);
            if (cursor == null) {
                return null;
            }
            if (!cursor.moveToNext()) {
                cursor.close();
                return null;
            }
            int columnPosition = 0;
            for (Column column : columns) {
                if (column.column_position == 0) {
                    column.column_position = columnPosition;
                }
                mapper.read(cursor, column, data);
                columnPosition++;
            }
            return data;
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Generic method to get a table entry
     *
     * @param database
     * @param id
     * @return the cursor for the object
     */
    @Override
    public Cursor getEntry(Database database, long id) throws DBException {
        Cursor result;
        String[] params = { String.valueOf(id) };
        try {
            result = database.getDatabase().query(getTableName(), getProjection(), getIdField() + "=?",
                    params, null, null, null);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Find an entry where the given column matches the given value.
     * @param database
     * @param columnName
     * @param columnValue
     * @return the cursor for the object
     */
    @Override
    public Cursor getEntry(Database database, String columnName, String columnValue) throws DBException {
        Cursor result;
        String[] params = { columnValue };
        try {
            result = database.getDatabase().query(getTableName(), getProjection(), columnName + "=?",
                    params, null, null, null);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
	    }
        return result;
    }

    @Override
    public T updateEntry(Database database, T data, Object key) {
        return null;
    }

    /**
     * Update the table with the given column data.
     * @param database
     * @param data
     * @param key
     * @return # of items updated
     */
    @Override
    public long updateEntry(Database database, List<String> data, Object key) throws DBException {
        ContentValues cv = new ContentValues();
        int columnSize = columns.size();
        int dataSize = data.size();
        // Assume the first column is the id column
        for (int i=0; i < dataSize && i < columnSize; i++) {
            cv.put(columns.get(i).getName(), data.get(i));
        }
        return updateEntry(database, cv, key);
    }

    /**
     * Update the table with the given column data. 1st column is the id.
     * @param database
     * @param data
     * @param key
     * @return # of items updated
     */
    @Override
    public long updateEntry(Database database, ContentValues data, Object key) throws DBException {
        try {
            String[] whereArgs = {String.valueOf(key)};
            return database.getDatabase().update(getTableName(), data, getIdField() + "=?", whereArgs);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
    }

    /**
     * Update the table with the given key using a mapper.
     * @param database
     * @param data
     * @param key
     * @param mapper
     * @return # of items updated
     */
    public long updateEntry(Database database, T data, Object key, DataMapper<T> mapper) throws DBException {
        try {
            String[] whereArgs = {String.valueOf(key)};
            int columnPosition = 0;
            ContentValues cv = new ContentValues();
            for (Column column : columns) {
                if (column.column_position == 0) {
                    column.column_position = columnPosition;
                }
                mapper.write(cv, column, data);
                columnPosition++;
            }
            return database.getDatabase().update(getTableName(), cv, getIdField() + "=?", whereArgs);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
    }

    /**
     * Update the table with the given key using a mapper.
     * @param database
     * @param data
     * @param columnName
     * @param columnValue
     * @param mapper
     * @return # of items updated
     */
    public long updateEntry(Database database, T data, String columnName, String columnValue, DataMapper<T> mapper) throws DBException {
        try {
            String[] whereArgs = {columnValue};
            int columnPosition = 0;
            ContentValues cv = new ContentValues();
            for (Column column : columns) {
                if (column.column_position == 0) {
                    column.column_position = columnPosition;
                }
                mapper.write(cv, column, data);
                columnPosition++;
            }
            return database.getDatabase().update(getTableName(), cv, columnName + "=?", whereArgs);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
    }

    /**
    * Update the table with the given content values and where information.
    * @param database
    * @param cv
    * @param whereClause
    * @param whereArgs
    * @return # of items updated
    */
    @Override
    public long updateEntryWhere(Database database, ContentValues cv, String whereClause,
            String[] whereArgs) throws DBException {
        try {
            return database.getDatabase().update(getTableName(), cv, whereClause, whereArgs);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
	    }
    }

    /**
     * Get all entries and return a list of items.
     * @param database
     * @param cls
     * @param mapper
     * @return List<T>
     */
    public List<? extends T> getAllEntries(Database database, Class<? extends T> cls, DataMapper<T> mapper) throws DBException {
        Cursor cursor = null;
        List<T> dataList = new ArrayList<T>();
        try {
            cursor = database.getDatabase().query(getTableName(), getProjection(), null, null, null,
                    null, null);
            if (cursor == null) {
                return dataList;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                return dataList;
            }
            do  {
                int columnPosition = 0;
                T data = cls.newInstance();
                for (Column column : columns) {
                    if (column.column_position == 0) {
                        column.column_position = columnPosition;
                    }
                    mapper.read(cursor, column, data);
                    columnPosition++;
                }
                dataList.add(data);
            } while (cursor.moveToNext());
        } catch (SQLiteException | IllegalAccessException | InstantiationException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        } finally {
			if (cursor != null) {
				cursor.close();
			}
        }
        return dataList;
    }

     /**
     * Get all the entries that match the given column value.
     * @param database
     * @param cls
     * @param columnName
     * @param columnValue
     * @param mapper
     * @return List<T>
     */
    public List<T> getAllEntriesWhere(Database database, Class<T> cls, String columnName, String columnValue, DataMapper<T> mapper)
		throws DBException {
        Cursor cursor = null;
        List<T> dataList = new ArrayList<T>();
        String[] params = { String.valueOf(columnValue) };
        try {
			cursor = database.getDatabase().query(getTableName(), getProjection(), columnName + " LIKE ?",
                    params, null,
                    null, null);
            if (cursor == null) {
                return dataList;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                return dataList;
            }
            do  {
                int columnPosition = 0;
                T data = cls.newInstance();
                for (Column column : columns) {
                    if (column.column_position == 0) {
                        column.column_position = columnPosition;
                    }
                    mapper.read(cursor, column, data);
                    columnPosition++;
                }
                dataList.add(data);
            } while (cursor.moveToNext());
        } catch (SQLiteException | InstantiationException | IllegalAccessException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        } finally {
			if (cursor != null) {
				cursor.close();
			}
        }
        return dataList;
    }

    /**
     * Get all entries with multiple where values
     * @param database
     * @param cls
     * @param columnValues
     * @param mapper
     * @return
     * @throws DBException
     */
    public List<T> getAllEntriesWhere(Database database, Class<T> cls, List<ColumnValue> columnValues, DataMapper<T> mapper)
		throws DBException {
        Cursor cursor = null;
        List<T> dataList = new ArrayList<T>();
        StringBuilder columnBuilder = new StringBuilder();
        String[] values = new String[columnValues.size()];
        int count=0;
        for (ColumnValue field : columnValues) {
            columnBuilder.append(field.fieldName).append("=?");
            values[count++] = field.value;
            if (count < columnValues.size()) {
                columnBuilder.append(" AND ");
            }
        }
        try {
			cursor = database.getDatabase().query(getTableName(), getProjection(), columnBuilder.toString(),
                    values, null,
                    null, null);
            if (cursor == null) {
                return dataList;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                return dataList;
            }
            do  {
                int columnPosition = 0;
                T data = cls.newInstance();
                for (Column column : columns) {
                    if (column.column_position == 0) {
                        column.column_position = columnPosition;
                    }
                    mapper.read(cursor, column, data);
                    columnPosition++;
                }
                dataList.add(data);
            } while (cursor.moveToNext());
        } catch (SQLiteException | InstantiationException | IllegalAccessException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        } finally {
			if (cursor != null) {
				cursor.close();
			}
        }
        return dataList;
    }

    /**
     * Get all entries with the given where clause and args
     * @param database
     * @param whereClause
     * @param whereArgs
     * @param cls
     * @param mapper
     * @return List<T>
     */
    public List<T> getAllEntriesWhere(Database database, String whereClause, String[] whereArgs, Class<T> cls, DataMapper<T> mapper)
		throws DBException {
        Cursor cursor = null;
        List<T> dataList = new ArrayList<T>();
        try {
            cursor = database.getDatabase().query(getTableName(), getProjection(), whereClause,
                                                  whereArgs, null,
                                                  null, null);
            if (cursor == null) {
                return dataList;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                return dataList;
            }
            do  {
                int columnPosition = 0;
                T data = cls.newInstance();
                for (Column column : columns) {
                    if (column.column_position == 0) {
                        column.column_position = columnPosition;
                    }
                    mapper.read(cursor, column, data);
                    columnPosition++;
                }
                dataList.add(data);
            } while (cursor.moveToNext());
        } catch (SQLiteException | InstantiationException | IllegalAccessException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dataList;
    }

    /**
     * Return a cursor with all entries
     * @param database
     * @return Cursor
     */
    @Override
    public Cursor getAllEntries(Database database) throws DBException {
        Cursor cursor;
        try {
            cursor = database.getDatabase().query(getTableName(), getProjection(), null, null, null,
                    null, null);
        } catch (SQLiteException e) {
            Logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage(), e);
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    /**
     * Get all entries and return a list of items.
     * @param database
     * @param cls
     * @return List<T>
     */
    public List<T> getAllEntries(Database database, Class<T> cls) {
        return null;
    }
}
