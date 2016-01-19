
package com.mastertechsoftware.easysqllibrary.sql.cookies;

import android.database.sqlite.SQLiteDatabase;

import com.mastertechsoftware.easysqllibrary.sql.Database;
import com.mastertechsoftware.easysqllibrary.sql.Table;

/**
 * Database with a cookie Table.
 */
public class CookieDatabase extends Database {
    private Table cookieTable;

    /**
     * Create a database with the given sql database
     * 
     * @param database
     */
    public CookieDatabase(SQLiteDatabase database) {
        super(database);
        cookieTable = new CookieTable();
        addTable(cookieTable);
    }
}
