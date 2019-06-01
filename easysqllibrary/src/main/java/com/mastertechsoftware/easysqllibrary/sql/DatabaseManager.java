package com.mastertechsoftware.easysqllibrary.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;

import com.mastertechsoftware.easysqllibrary.sql.upgrade.UpgradeStrategy;
import com.mastertechsoftware.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *  Manager for handling multiple reflection based databases
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    protected Map<String, ReflectionDBHelper> databases = new HashMap<String, ReflectionDBHelper>();
    protected Context context;
	private ReflectionDBHelper reflectionDBHelper;

	/**
     * Create new database manager with the given context (usually application)
     * @param context
     * @return DatabaseManager
     */
    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    /**
     * Return the current database manager
     * @return DatabaseManager
     */
    public static DatabaseManager getInstance() {
        return instance;
    }

    protected DatabaseManager(Context context) {
        this.context = context;
    }

    /**
     * Add a new database
     * @param dbName
     * @param mainTableName
     * @param types
     */
    public void addDatabase(String dbName, String mainTableName, Class<? extends ReflectTableInterface>... types) throws DBException {
        addDatabase(dbName, mainTableName, 1, types);
    }

	/**
	 * Set the upgrade strategy
	 * @param upgradeStrategy
	 */
	public void setUpgradeStrategy(UpgradeStrategy upgradeStrategy) {
		reflectionDBHelper.setUpgradeStrategy(upgradeStrategy);
	}

	/**
	 * Add a database, specifying the version number
	 * @param dbName
	 * @param mainTableName
	 * @param version
	 * @param types
	 */
    public void addDatabase(String dbName, String mainTableName, int version, Class<? extends ReflectTableInterface>... types) throws DBException {
        reflectionDBHelper = new ReflectionDBHelper(context, dbName, mainTableName, version, types);
        databases.put(dbName, reflectionDBHelper);
		createDatabase(dbName);
	}

	/**
	 * After deleting database, readd the database
	 * @param dbName
	 * @param mainTableName
	 * @param version
	 * @param types
	 */
    public void reAddDatabase(String dbName, String mainTableName, int version, Class<? extends ReflectTableInterface>... types) {
		ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper != null) {
			databases.remove(reflectionDBHelper);
		}
        reflectionDBHelper = new ReflectionDBHelper(context, dbName, mainTableName, version, types);
        databases.put(dbName, reflectionDBHelper);
    }

    /**
     * Add a new item
     * @param dbName
     * @param type
     * @param data
     * @return id of added item. -1 if there was an error
     */
    public long addItem(String dbName, Class<? extends ReflectTableInterface> type, ReflectTableInterface data) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return -1;
		}
        return reflectionDBHelper.addItem(type, data);
    }


    /**
     * Update an existing item
     * @param dbName
     * @param type
     * @param data
     */
    public void updateItem(String dbName, Class<? extends ReflectTableInterface> type, ReflectTableInterface data) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
        reflectionDBHelper.updateItem(type, data);
    }

    /**
     * Update a table with the given where clause
     * @param dbName
     * @param type
     * @param cv
     * @param whereClause
     * @param whereArgs
     * @return result
     */
    public long updateEntryWhere(String dbName, Class<? extends ReflectTableInterface> type, ContentValues cv, String whereClause,
                                String[] whereArgs) {

        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return -1;
		}
        return reflectionDBHelper.updateEntryWhere(type, cv, whereClause, whereArgs);
    }

    /**
     * Delete all items that match the query
     * @param dbName
     * @param type
     * @param columnName
     * @param columnValue
     */
    public void deleteItemWhere(String dbName, Class<? extends ReflectTableInterface> type, String columnName, String columnValue) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
        reflectionDBHelper.deleteItemWhere(type, columnName, columnValue);
    }

	/**
	 * Delete the item with the given id
	 * @param dbName
	 * @param type
	 * @param id
	 */
    public void deleteItem(String dbName, Class<? extends ReflectTableInterface> type, int id) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
        reflectionDBHelper.deleteItem(type, id);
    }

    /**
     * Remove all items of the given type
     * @param dbName
     * @param type
     */
    public void deleteAllItems(String dbName, Class<? extends ReflectTableInterface> type) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
        reflectionDBHelper.deleteAllItems(type);
    }

    /**
     * Return a list of all items in the database
     * @param dbName
     * @param type
     * @return List of items
     */
    public List<? extends ReflectTableInterface> getAllItems(String dbName, Class<? extends ReflectTableInterface> type) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
        return reflectionDBHelper.getAllItems(type);
    }

    /**
     * Return a list of items that match the query on the given column
     * @param dbName
     * @param type
     * @param columnName
     * @param columnValue
     * @return List of items
     */
    public List<? extends ReflectTableInterface> getItemsWhere(String dbName, Class<? extends ReflectTableInterface> type, String columnName, String columnValue) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
        return reflectionDBHelper.getItemsWhere(type, columnName, columnValue);
    }

	/**
	 * Get all entries with multiple where values
	 * @param dbName
	 * @param type
	 * @param fields
	 * @return
	 */
    public List<? extends ReflectTableInterface> getItemsWhere(String dbName, Class<? extends ReflectTableInterface> type, List<ColumnValue> fields) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
        return reflectionDBHelper.getItemsWhere(type, fields);
    }

	/**
	 * Get a single item
	 * @param dbName
	 * @param type
	 * @param id
	 * @return Object
	 */
	public Object getItem(String dbName, Class<? extends ReflectTableInterface> type, long id) {
		ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
		try {
			return reflectionDBHelper.getItem(type, id, type.newInstance());
		} catch (InstantiationException e) {
			Logger.error("Problems Creating object of type " + type.getName(), e);
		} catch (IllegalAccessException e) {
			Logger.error("Problems Creating object of type " + type.getName(), e);
		}
		return null;
	}

    /**
     * Query the database with all of the options available
     * @param dbName
     * @param type
     * @param distinct
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return Cursor
     * @throws DBException
     */
    public Cursor query(String dbName, Class<? extends ReflectTableInterface> type, boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) throws DBException {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
        return reflectionDBHelper.query(type, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

	/**
	 * Query the database with most of the options available
	 * @param dbName
	 * @param type
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
	 * @return Cursor
	 * @throws DBException
	 */
    public Cursor query(String dbName, Class<? extends ReflectTableInterface> type, boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit, CancellationSignal cancellationSignal) throws DBException {

        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
		return reflectionDBHelper.query(type, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
    }

	/**
	 * Query the database with less of the options available
	 * @param dbName
	 * @param type
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return Cursor
	 * @throws DBException
	 */
    public Cursor query(String dbName, Class<? extends ReflectTableInterface> type, String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy)  throws DBException {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
        return reflectionDBHelper.query(type, table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

	/**
	 * Query the database with main options
	 * @param dbName
	 * @param type
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return Cursor
	 * @throws DBException
	 */
    public Cursor query(String dbName, Class<? extends ReflectTableInterface> type, String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit)  throws DBException {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
        return reflectionDBHelper.query(type, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * Execute sql statement. Be careful.
     */
    public void execSQL(String dbName, Class<? extends ReflectTableInterface> type, String sql) throws DBException {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
        reflectionDBHelper.execSQL(type, sql);
    }

    /**
     * Delete the whole database
     * @param dbName
     */
    public void deleteDatabase(String dbName) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
        reflectionDBHelper.deleteDatabase();
        databases.remove(dbName);
    }


	/**
	 * Delete the entry in the sequence table for the given table
	 * Done usually after deleting the table
	 * @param dbName
	 * @param table
	 * @throws DBException
	 */
	public void deletePrimaryKey(String dbName, String table) {
		ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
		reflectionDBHelper.deletePrimaryKey(table);
	}

	/**
	 * Set the primary key to the given value for the given table
	 * @param dbName
	 * @param table
	 * @param startingId
	 * @throws DBException
	 */
	public void setPrimaryKey(String dbName, String table, int startingId){
		ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
		reflectionDBHelper.setPrimaryKey(table, startingId);
	}

	/**
	 * Get a specific item that has the given column name and value
	 * @param dbName
	 * @param type
	 * @param columnName
	 * @param columnValue
	 * @return
	 */
	public Object getItemWhere(String dbName, Class<? extends ReflectTableInterface> type, String columnName, String columnValue) {
		ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
		return reflectionDBHelper.getItemWhere(type, columnName, columnValue);
	}

	/**
	 * Return the current version of the database
	 * @param dbName
	 * @return
	 */
	public int getCurrentVersion(String dbName) {
		ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return 0;
		}
		return reflectionDBHelper.getCurrentVersion();
	}

	/**
	 * Create the database if it was dropped.
	 * @throws DBException
	 */
	public void createDatabase(String dbName) throws DBException {
		ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return;
		}
		reflectionDBHelper.createDatabase();
	}

	/**
	 * Get a list of tables for the given database
	 * @param dbName
	 * @return List of tables
	 */
    public List<Table> getTables(String dbName) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
		return reflectionDBHelper.getDatabase().getTables();
    }

	/**
	 * Get the table for the given db and class
	 * @param dbName
	 * @param type
	 * @return Table
	 */
    public Table getTable(String dbName, Class<? extends ReflectTableInterface> type) {
        ReflectionDBHelper reflectionDBHelper = databases.get(dbName);
		if (reflectionDBHelper == null) {
			Logger.error("Could not find helper for database " + dbName);
			return null;
		}
        return reflectionDBHelper.getTable(type);
    }

}
