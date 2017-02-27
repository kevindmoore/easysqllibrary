
package com.mastertechsoftware.easysqllibrary.sql.cookies;

import com.mastertechsoftware.easysqllibrary.sql.Database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Database with a cookie Table.
 */
public class CookieDatabase extends Database {

    /**
     * Create a database with the given sql database
     * 
     * @param database
     */
    public CookieDatabase(SQLiteDatabase database) {
        super(database);
        addTable(new CookieTable());
    }
}
