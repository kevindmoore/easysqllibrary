package com.mastertechsoftware.easysqllibrary.sql;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to describe a SQL Table. Holds the columns, table name and projection.
 */
public abstract class Table<T> {
    public static final String ID = "_id";
	protected String tableName;
    protected String idField = ID;
	protected List<Column> columns = new ArrayList<Column>();
	protected String[] projection;
    protected int version = 1;

  /**
   * Constructor
   */
	public Table() {
	}

  /**
   * Constructor
   * @param tableName
   */
	public Table(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Create a table with the given columns and table name
	 * @param columns
	 * @param tableName
	 */
	public Table(List<Column> columns, String tableName) {
		this.columns = columns;
		this.tableName = tableName;
	}

    /**
	 * Set the version for this table
     * @param version
     */
    public void setupVersion(int version) {
		this.version = version;
    }

    /**
	 * Get the list of columns for this table
	 * @return list of columns
	 */
	public List<Column> getColumns() {
		return columns;
	}

    /**
     * Return the names of the columns
     * @return List<String>
     */
    public List<String> getColumnNames() {
        ArrayList<String> columnNames = new ArrayList<String>();
        for (Column column : columns) {
            columnNames.add(column.getName());
        }
        return columnNames;
    }

	/**
	 * Replace the current columns with this list
	 * @param columns
	 */
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	/**
     * Get the column at the given index.
     * @param index
     * @return Column
     */
    public Column getColumn(int index) {
        if (index < columns.size()) {
            return null;
        }
        return columns.get(index);
    }

    /**
     * Return a column with the given name
     * @param columnName
     * @return
     */
    public Column getColumn(String columnName) {
        for (Column column : columns) {
            if (columnName.equalsIgnoreCase(column.getName())) {
                return column;
            }
        }
        return null;
    }

    /**
	 * Get table name
	 * @return name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * set the table name
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Add a new column
	 * @param column
	 */
	public void addColumn(Column column) {
		columns.add(column);
	}

	/**
	 * Remove a column
	 * @param column
	 */
	public void removeColumn(Column column) {
		columns.remove(column);
	}

    /**
     * Clear all columns
     */
	public void removeColumns() {
		columns.clear();
	}

	/**
	 * Create a string to create a new table.
	 * @return sql string
	 */
	public String getCreateTableString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
		boolean firstColumn = true;
		for (Column column : columns) {
			if (!firstColumn) {
				builder.append(", ");
			}
			firstColumn = false;
			builder.append(column.getCreateString());
		}
		builder.append(")");
		return builder.toString();

	}

	/**
	 * Get the projection needed for sql queries
	 * @return Projection
	 */
	public String[] getProjection() {
		if (projection == null) {
			projection = new String[columns.size()];
			int count = 0;
			for (Column column : columns) {
				projection[count++] = column.getName();
			}
		}
		return projection;
	}

	/**
	 * Generic method to insert a table entry
	 *
	 * @param database
	 * @param data
	 * @return the object created
	 */
	public abstract T insertEntry(Database database, T data);

	/**
     * Generic method to insert a table entry
     * @param database
     * @param data
     * @return the id of the object created
     */
    public abstract long insertEntry(Database database, List<String> data) throws DBException;

    /**
     * Generic method to insert a table entry
     * @param database
     * @param data
     * @return the id of the object created
     */
    public abstract long insertEntry(Database database, ContentValues data) throws DBException;

    /**
     * Insert a new entry into the db using a mapper
     * @param database
     * @param data
     * @param mapper
     * @return new id
     */
    public abstract long insertEntry(Database database, T data, DataMapper<T> mapper) throws DBException;
        /**
          * Generic method to delete a table entry
          *
          * @param database
          * @param key
          */
	public abstract long deleteEntry(Database database, Object key) throws DBException;

	/**
	 * Delete the entry with the given where clause and values
	 * @param database
	 * @param whereClause
	 * @param whereArgs
	 */
	public abstract long deleteEntryWhere(Database database, String whereClause, String[] whereArgs) throws DBException;

	/**
	 * Delete the entry with the given where column and value
	 * @param database
	 * @param columnName
	 * @param columnValue
	 */
	public abstract long deleteEntryWhere(Database database, String columnName, String columnValue) throws DBException;


		/**
		 * Delete all table entries.
		 * @param database
		 */
	public abstract void deleteAllEntries(Database database) throws DBException;

	/**
	 * Generic method to get a table entry
	 *
	 * @param database
	 * @param key
     * @return the object found
	 */
	public abstract T getEntry(Database database, Object key);

	/**
     * Generic method to get a table entry
     *
     * @param database
     * @param id
     * @return the cursor for the object
     */
    public abstract Cursor getEntry(Database database, long id) throws DBException;

    /**
     * Find an entry where the given column matches the given value.
     * @param database
     * @param columnName
     * @param columnValue
     * @return the cursor for the object
     */
    public abstract Cursor getEntry(Database database, String columnName, String columnValue) throws DBException;

    /**
	 * Generic method to update a table entry
	 *
	 * @param database
	 * @param data
     * @param key
     * @return the object updated
	 */
	public abstract T updateEntry(Database database, T data, Object key);

	/**
     * Generic method to update a table entry
     * @param database
     * @param data
     * @param key
     * @return the number of objects updated
     */
    public abstract long updateEntry(Database database, List<String> data, Object key) throws DBException;

    /**
     * Generic method to update a table entry
     * @param database
     * @param data
     * @param key
     * @return the number of objects updated
     */
    public abstract long updateEntry(Database database, ContentValues data, Object key) throws DBException;

    /**
	 * Update the entry with the given where clause and values
	 * @param database
	 * @param cv
	 * @param whereClause
	 * @param whereArgs
	 * @return number of items updated
	 */
	public abstract long updateEntryWhere(Database database, ContentValues cv, String whereClause, String[] whereArgs) throws DBException;

	/**
	 * Return the string identifying the id field. Usually _id
     * 
	 * @return field string
	 */
    public String getIdField() {
        return idField;
    }

    /**
     * Set the id field for this table. Usually "_id"
     * @param idField
     */
    public void setIdField(String idField) {
        this.idField = idField;
    }


    /**
     * Return a cursor with all entries
     * @param database
     * @return Cursor
     */
    public abstract Cursor getAllEntries(Database database) throws DBException;

    /**
     * Return a cursor with all entries
     * @param database
     * @return List<T>
     */
    public abstract List<T> getAllEntries(Database database, Class<T> cls);

	/**
	 * Helper method to create a set of content values. can string together calls.
	 * @param cv
	 * @param field
	 * @param value
	 * @return ContentValues
	 */
	public ContentValues addContentValue(ContentValues cv, String field, String value) {
		if (cv == null) {
			cv = new ContentValues();
		}
		cv.put(field, value);
		return cv;
	}

    /**
     * Return the version # for this table
     * 
     * @return version #
     */
    public int getVersion() {
        return version;
    }

	/**
     * Set the version # for this table
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Helper method to create a set of content values. can string together
     * calls.
	 */
	public static class ContentValueBuilder {
		ContentValues contentValues;

		/**
		 * Helper method to create a set of content values. can string together calls.
		 * @param field
		 * @param value
		 * @return ContentValueBuilder
		 */
		public ContentValueBuilder addContentValue(String field, String value) {
			if (contentValues == null) {
				contentValues = new ContentValues();
			}
			contentValues.put(field, value);
			return this;
		}

		/**
		 * Call this last to get the content value list
		 * @return
		 */
		public ContentValues build() {
			return contentValues;
		}
	}

	@Override
	public String toString() {
		return "Table{" +
				"tableName='" + tableName + '\'' +
				", idField='" + idField + '\'' +
				", columns=" + columns +
				", projection=" + Arrays.toString(projection) +
				", version=" + version +
				'}';
	}
}
