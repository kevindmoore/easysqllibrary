
package com.mastertechsoftware.easysqllibrary.sql.cookies;

import com.mastertechsoftware.easysqllibrary.sql.BaseDatabaseHelper;
import com.mastertechsoftware.easysqllibrary.sql.DBException;
import com.mastertechsoftware.logging.Logger;

import org.apache.http.client.CookieStore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Database Helper for Web cookies
 */
public class CookieDatabaseHelper extends BaseDatabaseHelper {
    private static final String DATABASE = "cookies.db";
    private CookieTable cookieTable;
    // Lock used to serialize access to this API.
    private final ReentrantLock mLock = new ReentrantLock();
	protected static final int version = 1;

    /**
     * Create a helper object to create, open, and/or manage a database. This
     * method always returns very quickly. The database is not actually created
     * or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     * 
     * @param context to use to open or create the database
     */
    public CookieDatabaseHelper(Context context) {
        super(context, DATABASE, CookieTable.TABLE_NAME, version);
    }


    /**
     * Create our database objects with the current SQLite db.
     */
	protected void createLocalDB() {
		localDatabase = new CookieDatabase(sqLiteDatabase);
        cookieTable = (CookieTable) localDatabase.getTable(CookieTable.TABLE_NAME);
    }

    /**
     * If the database version changes, migrate the data to the new scheme
     * 
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		sqLiteDatabase = db;
        localDatabase = new CookieDatabase(db);
        // For now, just delete
        if (oldVersion != newVersion) {
			try {
				dropDatabase();
			} catch (DBException e) {
				Logger.error(this, "Problems dropping database during upgrade");
			}
        }
    }

    /**
     * Delete all cookies
     */
    public void deleteAllCookies() {
        // Lock it!
        mLock.lock();
        // Wrap the whole thing so we can make sure to unlock in
        // case something throws.
        try {
			startTransaction();
            cookieTable.deleteAllEntries(localDatabase);
        } catch (DBException e) {
			Logger.error(this, "Problems deleting cookies");
		} finally {
			endTransaction();
            mLock.unlock();
        }
    }

    /**
     * Add all cookies from Cookie store to db
     * 
     * @param cookieStore
     */
    public void addCookieStore(CookieStore cookieStore) {
        // Lock it!
        mLock.lock();
        // Wrap the whole thing so we can make sure to unlock in
        // case something throws.
        try {
			startTransaction();
            deleteAllCookies();
            cookieTable.addCookieStore(localDatabase, cookieStore);
		} catch (DBException e) {
			Logger.error(this, "Problems adding cookies");
        } finally {
			endTransaction();
            mLock.unlock();
        }
    }

    /**
     * Get a cookie store from our database.
     * 
     * @return CookieStore
     */
    public CookieStore getCookieStore() {
        // Lock it!
        mLock.lock();
        // Wrap the whole thing so we can make sure to unlock in
        // case something throws.
        try {
            return cookieTable.getCookieStore(localDatabase);
        } finally {
            mLock.unlock();
        }
    }
}
