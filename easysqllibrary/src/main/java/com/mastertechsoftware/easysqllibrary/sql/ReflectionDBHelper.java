package com.mastertechsoftware.easysqllibrary.sql;

import com.mastertechsoftware.easysqllibrary.sql.upgrade.UpgradeStrategy;
import com.mastertechsoftware.logging.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Hold all the pieces needed for Handling a reflection db.
 */
public class ReflectionDBHelper {

	protected List<CRUDHelper<ReflectTableInterface>> crudHelpers = new ArrayList<CRUDHelper<ReflectTableInterface>>();
    protected Database database;
    protected BaseDatabaseHelper databaseHelper;
    protected Map<Class, Integer> classMapper = new HashMap<Class, Integer>();
	protected boolean debugging = false;
	protected int version = 1;

	/**
	 * Constructor
	 * @param context
	 * @param dbName
	 * @param mainTableName
	 * @param version
	 * @param types
	 */
	public ReflectionDBHelper(Context context, String dbName, String mainTableName, int version, Class<? extends ReflectTableInterface>... types) {
		Logger.setDebug(ReflectionDBHelper.class.getSimpleName(), debugging);
		this.version = version;
		databaseHelper = new BaseDatabaseHelper(context, dbName, mainTableName, version);
		database = new Database();
		database.setVersion(version);
		databaseHelper.setLocalDatabase(database);
 		for (Class<? extends ReflectTableInterface> reflectClass : types) {
			addTable(reflectClass);
		}
//		checkAndCreateTables();
	}

	/**
	 * Constructor
	 * @param context
	 * @param dbName
	 * @param mainTableName
	 * @param types
	 */
	public ReflectionDBHelper(Context context, String dbName, String mainTableName, Class<? extends ReflectTableInterface>... types) {
		this(context, dbName, mainTableName, 1, types);
    }

	/**
	 * Return the current version of the database
	 * @return version
	 */
	public int getCurrentVersion() {
		return databaseHelper.getDBVersion();
	}

	/**
	 * Set the upgrade strategy
	 * @param upgradeStrategy
	 */
	public void setUpgradeStrategy(UpgradeStrategy upgradeStrategy) {
		databaseHelper.setUpgradeStrategy(upgradeStrategy);
	}

	/**
	 * Add a new Table
	 * @param reflectClass
	 */
    private void addTable(Class<? extends ReflectTableInterface> reflectClass) {
        try {
            ReflectTable<ReflectTableInterface> table = new ReflectTable<ReflectTableInterface>(reflectClass.newInstance(), database);
			table.setVersion(version);
            if (!database.tableExists(table.getTableName())) {
                Logger.debug("Adding " + table.toString());
                database.addTable(table);
                classMapper.put(reflectClass, crudHelpers.size()); // Do this before adding so it's zero based
                CRUDHelper<ReflectTableInterface> crudHelper = new CRUDHelper<>(table, databaseHelper);
                crudHelpers.add(crudHelper);
                List<Field> reflectFields = table.getReflectFields();
                for (Field reflectField : reflectFields) {
                    addTable(((Class<? extends ReflectTableInterface>) reflectField.getType()));
                }
            }
        } catch (InstantiationException e) {
            Logger.error(this, "Problems creating table", e);
        } catch (IllegalAccessException e) {
            Logger.error(this, "Problems creating table", e);
        }
    }

	/**
	 * Check to see if all tables exist and create them if they don't
	 */
	private void checkAndCreateTables() {
		try {
			final List<Table> tables = database.getTables();
			for (Table table : tables) {
				if (!databaseHelper.tableExists(table.tableName)) {
					databaseHelper.createTable(table);
				}
			}
		} catch (DBException e) {
			Logger.error(this, "Problems creating table", e);
		}
	}

	/**
	 * Return the Database object
	 * @return
	 */
    public Database getDatabase() {
        return database;
    }

	/**
	 * Return the database helper
	 * @return
	 */
    public BaseDatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

	/**
	 * Get the CRUDHelper
	 * @param position
	 * @return
	 */
    public CRUDHelper<ReflectTableInterface> getCrudHelper(int position) {
        return crudHelpers.get(position);
    }

	/**
	 * Get the CRUDHelper
	 * @param tableName
	 * @return
	 */
    public CRUDHelper<ReflectTableInterface> getCrudHelper(String tableName) {
        for (CRUDHelper<ReflectTableInterface> crudHelper : crudHelpers) {
            if (crudHelper.getTable().getTableName().equalsIgnoreCase(tableName)) {
                return crudHelper;
            }
        }
        return null;
    }

	/**
	 * Delete the entire database
	 */
    public void deleteDatabase() {
        try {
            databaseHelper.dropDatabase();
        } catch (DBException e) {
            Logger.error(this, "Problems deleting database", e);
        }
    }

	/**
	 * Delete the entry in the sequence table for the given table
	 * Done usually after deleting the table
	 * @param table
	 * @throws DBException
	 */
	public void deletePrimaryKey(String table) {
		try {
			databaseHelper.deletePrimaryKey(table);
		} catch (DBException e) {
			Logger.error(this, "Problems deleting primary key for table " + table, e);
		}
	}

	/**
	 * Set the primary key to the given value for the given table
	 * @param table
	 * @param startingId
	 * @throws DBException
	 */
	public void setPrimaryKey(String table, int startingId) {
		try {
			databaseHelper.setPrimaryKey(table, startingId);
		} catch (DBException e) {
			Logger.error(this, "Problems setting primary key for table " + table, e);
		}
	}

	/**
	 * Add a single item
	 * @param type
	 * @param data
	 * @return new position
	 */
    public int addItem(Class<? extends ReflectTableInterface> type, ReflectTableInterface data) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return -1;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.addItem(data);
    }

	/**
	 * Update a single item
	 * @param type
	 * @param data
	 */
    public void updateItem(Class<? extends ReflectTableInterface> type, ReflectTableInterface data) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        crudHelper.updateItem(data, data.getId());
    }

    /**
     * Update a table with the given where clause
     * @param cv
     * @param whereClause
     * @param whereArgs
     * @return result
     */
    public int updateEntryWhere(Class<? extends ReflectTableInterface> type, ContentValues cv, String whereClause,
                                String[] whereArgs) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return -1;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.updateEntryWhere(cv, whereClause, whereArgs);
    }


	/**
	 * Delete a single item
	 * @param type
	 * @param id
	 */
    public void deleteItem(Class<? extends ReflectTableInterface> type, int id) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        crudHelper.deleteItem(id);
    }

	/**
	 * Delete a single item with the given column value
	 * @param type
	 * @param columnName
	 * @param columnValue
	 */
    public void deleteItemWhere(Class<? extends ReflectTableInterface> type, String columnName, String columnValue) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        crudHelper.deleteItemWhere(columnName, columnValue);
    }

	/**
	 * Remove all items for this class
	 * @param type
	 */
    public void deleteAllItems(Class<? extends ReflectTableInterface> type) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        crudHelper.deleteAllItems();
    }

	/**
	 * Get all items for this class
	 * @param type
	 * @return
	 */
    public List<? extends ReflectTableInterface> getAllItems(Class<? extends ReflectTableInterface> type) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return null;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.getItems(type);
    }

	/**
	 * Get all items for this class and value
	 * @param type
	 * @param columnName
	 * @param columnValue
	 * @return
	 */
    public List<? extends ReflectTableInterface> getItemsWhere(Class<? extends ReflectTableInterface> type, String columnName, String columnValue) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return null;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.getItemsWhere(type, columnName, columnValue);
    }

	/**
	 * Get all items where the given list of column/value strings is given in the ClassField list
	 * @param type
	 * @param fields
	 * @return
	 */
    public List<? extends ReflectTableInterface> getItemsWhere(Class<? extends ReflectTableInterface> type, List<ColumnValue> fields) {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return null;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.getItemsWhere(type, fields);
    }

	/**
	 * Query the db
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
	 * @return
	 * @throws DBException
	 */
    public Cursor query(Class<? extends ReflectTableInterface> type, boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) throws DBException {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return null;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

	/**
	 * Query the db
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
	 * @return
	 * @throws DBException
	 */
    public Cursor query(Class<? extends ReflectTableInterface> type, boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit, CancellationSignal cancellationSignal) throws DBException {

        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return null;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.query(distinct, table, columns,
                                selection, selectionArgs, groupBy,
                                having, orderBy, limit, cancellationSignal);
    }

	/**
	 * Query the db
	 * @param type
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
    public Cursor query(Class<? extends ReflectTableInterface> type, String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy)  throws DBException {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return null;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.query(table, columns,
                                selection, selectionArgs, groupBy,
                                having, orderBy);
    }

	/**
	 * Query the db
	 * @param type
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
    public Cursor query(Class<? extends ReflectTableInterface> type, String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit)  throws DBException {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return null;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        return crudHelper.query(table, columns,
                                selection, selectionArgs, groupBy,
                                having, orderBy, limit);
    }

    /**
     * Execute sql statement. Be careful.
     */
    public void execSQL(Class<? extends ReflectTableInterface> type, String sql) throws DBException {
        Integer position = classMapper.get(type);
        if (position == null) {
            Logger.error("Type " + type.getName() + " Not found");
            return;
        }
        CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
        crudHelper.execSQL(sql);
    }

	/**
	 * Get a single item with the given id
	 * @param type
	 * @param id
	 * @param newItem
	 * @return ReflectTableInterface
	 */
    public ReflectTableInterface getItem(Class<? extends ReflectTableInterface> type, long id, ReflectTableInterface newItem) {
        Integer position = classMapper.get(type);
        if (position == null) {
			Logger.error("Type " + type.getName() + " Not found");
			return null;
		}
		CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
		return crudHelper.getItem(id, newItem);
	}

	/**
	 * Get a single item with the given value
	 * @param type
	 * @param columnName
	 * @param columnValue
	 * @return ReflectTableInterface
	 */
	public ReflectTableInterface getItemWhere(Class<? extends ReflectTableInterface> type, String columnName, String columnValue) {
		Integer position = classMapper.get(type);
		if (position == null) {
			Logger.error("Type " + type.getName() + " Not found");
			return null;
		}
		CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
		try {
			return crudHelper.getItemWhere(type.newInstance(), columnName, columnValue);
		} catch (InstantiationException e) {
			Logger.error("Problems Creating object of type " + type.getName(), e);
		} catch (IllegalAccessException e) {
			Logger.error("Problems Creating object of type " + type.getName(), e);
		}
		return null;
	}

	/**
	 * Create the database if it was dropped.
	 * @throws DBException
	 */
	public void createDatabase() throws DBException {
		databaseHelper.createDatabase();
	}

	/**
	 * Get the table associated with this class
	 * @param type
	 * @return Table
	 */
	public Table getTable(Class<? extends ReflectTableInterface> type) {
		Integer position = classMapper.get(type);
		if (position == null) {
			Logger.error("Type " + type.getName() + " Not found");
			return null;
		}
		CRUDHelper<ReflectTableInterface> crudHelper = getCrudHelper(position);
		return crudHelper.getTable();
	}
}
