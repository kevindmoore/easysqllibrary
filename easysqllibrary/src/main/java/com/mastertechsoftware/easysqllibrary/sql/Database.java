package com.mastertechsoftware.easysqllibrary.sql;

import com.mastertechsoftware.logging.Logger;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to describe a SQL Database. Holds tables and SQLite Database.
 */
public class Database {
	protected List<Table> tables = new ArrayList<Table>();
	protected SQLiteDatabase database;
	protected int version = 1;

	/**
	 * Default Constructor. Set tables and database later
	 */
	public Database() {
	}

	/**
	 * Create a database with the given sql database
	 * @param database
	 */
	public Database(SQLiteDatabase database) {
		this.database = database;
	}

	/**
	 * Set the SQLiteDatabase
	 * @param database
	 */
	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}

	/**
	 * Set the versions for all tables
     * @param version
     */
    public void setupVersion(int version) {
		this.version = version;
        for (Table table : tables) {
            table.setupVersion(version);
        }
    }

	/**
	 * Return the version of this database
	 * @return version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Set the version
	 * @param version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * get a list of tables
	 * @return List<Table>
	 */
	public List<Table> getTables() {
		return tables;
	}

    /**
     * Set the list of tables.
     * @param tables
     */
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}


    /**
	 * Add a new table.
	 * @param table
	 */
	public void addTable(Table table) {
		tables.add(table);
	}

    /**
     * Check if the table alredy exists
     * @param tableName
     * @return
     */
    public boolean tableExists(String tableName) {
        for (Table table : tables) {
            if (tableName.equalsIgnoreCase(table.getTableName())) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Get the sql databae
	 * @return
	 */
	public SQLiteDatabase getDatabase() {
		return database;
	}

	/**
	 * Get the table with the given name.
	 * @param name
	 * @return Table
	 */
	public Table getTable(String name) {
		for (Table table : tables) {
			if (table.getTableName().equalsIgnoreCase(name)) {
				return table;
			}
		}
		return null;
	}

	/**
	 * Create the database from all the tables
	 */
	public void createDatabase() {
        for (Table table : tables) {
            String createTableString = table.getCreateTableString();
            try {
				Logger.debug(createTableString);
                database.execSQL(createTableString);
            } catch (SQLiteException e) {
                Logger.error(e.getMessage());
            }
        }
	}

    /**
     * Create a string that is a list of table creation statements
     * @return full table creation list
     */
    public String getCreateDatabaseString() {
        StringBuilder builder = new StringBuilder();
        for (Table table : tables) {
            String createTableString = table.getCreateTableString();
            builder.append(createTableString).append(";");
        }
        Logger.debug(this, "Creation string " + builder.toString());
        return builder.toString();
    }

	/**
	 * Drop all tables
	 */
	public void dropDatabase() {
		try {
			for (Table table : tables) {
				database.execSQL("DROP TABLE IF EXISTS " + table.getTableName());
			}

		} catch (SQLiteException e) {
			Logger.error(this, e.getMessage());
		}
	}

    /**
     * Execute sql statement. Be careful.
     * @param sql
     */
    public void execSQL(String sql) {
        try {
            database.execSQL(sql);
        } catch (SQLiteException e) {
            Logger.error(this, e.getMessage());
        }
    }

	/**
	 * Generic method to insert a table entry
	 *
	 * @param table
	 * @param data
	 * @return the object created
	 */
	public Object insertTableEntry(Table table, Object data) {
		return table.insertEntry(this, data);
	}

	/**
	 * Generic method to delete a table entry
	 *
	 * @param table
	 * @param data
	 */
	public void deleteTableEntry(Table table, Object data) throws DBException {
		table.deleteEntry(this, data);
	}

	/**
	 * Generic method to update a table entry
	 *
	 * @param table
	 * @param data
     * @param key
	 * @return the object created
	 */
	public Object updateTableEntry(Table table, Object data, Object key) {
		return table.updateEntry(this, data, key);
	}

	/**
	 * Generic method to get a table entry
	 *
	 * @param table
	 * @param data
	 * @return the object created
	 */
	public Object getTableEntry(Table table, Object data) {
		return table.getEntry(this, data);
	}

	/**
	 * Generic method to get a table entry
	 *
	 * @param table
	 * @return the object created
	 */
	public Object getAllTableEntries(Table table) throws DBException {
		return table.getAllEntries(this);
	}

	@Override
	public String toString() {
		return "Database{" +
				"tables=" + tables +
				", database=" + database +
				", version=" + version +
				'}';
	}
}
