package com.mastertechsoftware.easysqllibrary.sql;

import com.mastertechsoftware.logging.Logger;

import android.database.sqlite.SQLiteDatabase;
/**
 * Database to hold information on current and past database versions
 */
public class MetaDatabase extends Database {
    private MetaTable metaTable;

    public MetaDatabase(SQLiteDatabase database) {
        super(database);
        metaTable = new MetaTable(new Meta(), this);
        addTable(metaTable);
    }

    /**
     * Check to see if the database for that version exists
     * @param version
     * @param databaseName
     * @return
     */
    public boolean databaseExists(int version, String databaseName) throws DBException {
        return getMeta(version, databaseName) != null;
    }

    /**
     * Get the meta info for the given database and version
     * @param version
     * @param databaseName
     * @return
     */
    protected Meta getMeta(int version, String databaseName) throws DBException {
        return metaTable.getEntry(this, "version = ? and database = ?", new String[]{String.valueOf(version), databaseName},
                                        new Meta(), metaTable.getMapper());
    }

    /**
     * Add a new table entry for the given version, database and creation string
     * @param version
     * @param databaseName
     * @param creationString
     */
    public void addDatabaseEntry(int version, String databaseName, String creationString) throws DBException {
        Meta meta = new Meta();
        meta.creationString = creationString;
        meta.database = databaseName;
        meta.version = version;
        long num = metaTable.insertEntry(this, meta, metaTable.getMapper());
        if (num <= 0) {
            Logger.error(this, "Problems inserting meta data");
        }
    }
}
