package com.mastertechsoftware.easysqllibrary.sql;

import com.mastertechsoftware.logging.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.CancellationSignal;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
/**
 * This is a helper class that will do CRUD (Create, read, update, delete) operations
 */
public class CRUDHelper<T extends ReflectTableInterface> {
	// Lock used to serialize access to this API.
	protected final ReentrantLock mLock = new ReentrantLock();
	protected ReflectTable<T> table;
	protected Database database;
	protected BaseDatabaseHelper databaseHelper;
	protected boolean debugging = false;

	public CRUDHelper(ReflectTable<T> table, BaseDatabaseHelper databaseHelper) {
		Logger.setDebug(ReflectionDBHelper.class.getSimpleName(), debugging);
		this.table = table;
		this.database = databaseHelper.localDatabase;
		this.databaseHelper = databaseHelper;
	}

    /**
     * Get the table that this helper represents
     * @return ReflectTable<T>
     */
    public ReflectTable<T> getTable() {
        return table;
    }

    /**
     * Get the Database that this helper represents
     * @return Database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Get the BaseDatabaseHelper that this helper uses
     * @return BaseDatabaseHelper
     */
    public BaseDatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    /**
	 * Add a new Item
	 *
	 * @param item
	 * @return id
	 */
	public int addItem(T item) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			int id = table.insertEntry(database, item, table.getMapper());
			item.setId((int) id);
			return id;
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage());
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return -1;
	}

	/**
	 * Get the list of all items
	 * @return List<T>
	 */
	public List<? extends T> getItems(Class<? extends T> classItem) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return table.getAllEntries(database, classItem, table.getMapper());
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage());
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

    /**
     * Get items with where clause
     * @param classItem
     * @param columnName
     * @param columnValue
     * @return List of items
     */
	public List<? extends T> getItemsWhere(Class<? extends T> classItem, String columnName, String columnValue) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return table.getAllEntriesWhere(database, (Class<T>) classItem, columnName, columnValue, table.getMapper());
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage());
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

	/**
	 * Get all items where the given list of column/value strings is given in the ClassField list
	 * @param classItem
	 * @param fields
	 * @return List of items
	 */
	public List<? extends T> getItemsWhere(Class<? extends T> classItem, List<ColumnValue> fields) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return table.getAllEntriesWhere(database, (Class<T>) classItem, fields, table.getMapper());
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage());
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

	/**
	 * Return a specific item that matches the given column and value
	 * @param classItem
	 * @param columnName
	 * @param columnValue
	 * @return T
	 */
	public T getItemWhere(T classItem, String columnName, String columnValue) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return table.getEntry(database, classItem, columnName, columnValue, table.getMapper());
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

	/**
	 * Query the class
	 * @param distinct
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 * @throws DBException
	 */
	public Cursor query(boolean distinct, String table, String[] columns,
						String selection, String[] selectionArgs, String groupBy,
						String having, String orderBy, String limit) throws DBException {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return databaseHelper.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		} catch (DBException e) {
			Logger.error(this, "Problems starting  transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

	/**
	 * Query the class
	 * @param distinct
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @param cancellationSignal
	 * @return
	 * @throws DBException
	 */
	public Cursor query(boolean distinct, String table, String[] columns,
						String selection, String[] selectionArgs, String groupBy,
						String having, String orderBy, String limit, CancellationSignal cancellationSignal) throws DBException {

		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return databaseHelper.query(distinct, table, columns,
										selection, selectionArgs, groupBy,
										having, orderBy, limit, cancellationSignal);
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

	/**
	 * Query the class
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 * @throws DBException
	 */
	public Cursor query(String table, String[] columns, String selection,
						String[] selectionArgs, String groupBy, String having,
						String orderBy)  throws DBException {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return databaseHelper.query(table, columns,
										selection, selectionArgs, groupBy,
										having, orderBy);
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

	/**
	 * Query the class
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 * @throws DBException
	 */
	public Cursor query(String table, String[] columns, String selection,
						String[] selectionArgs, String groupBy, String having,
						String orderBy, String limit)  throws DBException {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return databaseHelper.query(table, columns,
										selection, selectionArgs, groupBy,
										having, orderBy, limit);
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

	/**
	 * Execute sql statement. Be careful.
	 */
	public void execSQL(String sql) throws DBException {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			databaseHelper.execSQL(sql);
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
	}
	/**
	 * Get the list of all items
	 * @return List<T>
	 */
	public List<T> getItems(Class<T> classItem, long id) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return table.getAllEntriesWhere(database, classItem, Table.ID, String.valueOf(id), table.getMapper());
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}

    /**
     * Get a Cursor for the table
     * @return Cursor
     */
    public Cursor getItemCursor() {
        // Lock it!
        mLock.lock();
        try {
            databaseHelper.startTransaction();
            return table.getAllEntries(database);
        } catch (DBException e) {
            Logger.error(this, "Problems starting transaction " + e.getMessage() );
        } finally {
            databaseHelper.endTransaction();
            mLock.unlock();
        }
        return null;

    }
	/**
	 * Delete Item
	 * @param id
	 */
	public void deleteItem(long id) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			int deleted = table.deleteEntryWhere(database, Table.ID, String.valueOf(id));
			if (deleted < 1) {
				Logger.error("Could not delete item with id " + id);
			}
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
	}

    /**
     * Delete an item where the column name = the column value
     * @param columnName
     * @param columnValue
     */
    public void deleteItemWhere(String columnName, String columnValue) {
        // Lock it!
        mLock.lock();
        try {
            databaseHelper.startTransaction();
            int deleted = table.deleteEntryWhere(database, columnName, columnValue);
            Logger.debug("Deleted " + deleted + " items ");
        } catch (DBException e) {
            Logger.error(this, "Problems starting transaction " + e.getMessage() );
        } finally {
            databaseHelper.endTransaction();
            mLock.unlock();
        }
    }

    public void deleteAllItems() {
        // Lock it!
        mLock.lock();
        try {
            databaseHelper.startTransaction();
            table.deleteAllEntries(database);
        } catch (DBException e) {
            Logger.error(this, "Problems starting transaction " + e.getMessage() );
        } finally {
            databaseHelper.endTransaction();
            mLock.unlock();
        }

    }

	public void updateItem(T item, long id) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			int result = table.updateEntry(database, item, id, table.getMapper());
            if (result <= 0) {
                Logger.error("Unable to update table " + table.getTableName());
            }
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
	}

	/**
	 * Update a table with the given where clause
	 * @param cv
	 * @param whereClause
	 * @param whereArgs
	 * @return result
	 */
	public int updateEntryWhere(ContentValues cv, String whereClause,
								String[] whereArgs) {

		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			int result = table.updateEntryWhere(database, cv, whereClause, whereArgs);
			if (result <= 0) {
				Logger.error("Unable to update table " + table.getTableName());
			}
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return -1;
	}

    /**
     * Return the columns name for our table
     * @return List<String> column names
     */
    public List<String> getColumnNames() {
        return table.getColumnNames();
    }

	/**
	 * Get an item with the given id
	 * @param id
	 * @param item
	 * @return Object
	 */
	public T getItem(long id, T item) {
		// Lock it!
		mLock.lock();
		try {
			databaseHelper.startTransaction();
			return table.getEntry(database, id, item, table.getMapper());
		} catch (DBException e) {
			Logger.error(this, "Problems starting transaction " + e.getMessage() );
		} finally {
			databaseHelper.endTransaction();
			mLock.unlock();
		}
		return null;
	}
}
