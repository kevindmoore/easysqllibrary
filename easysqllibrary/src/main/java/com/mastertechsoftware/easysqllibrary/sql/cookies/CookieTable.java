
package com.mastertechsoftware.easysqllibrary.sql.cookies;

import com.mastertechsoftware.easysqllibrary.sql.AbstractDataMapper;
import com.mastertechsoftware.easysqllibrary.sql.AbstractTable;
import com.mastertechsoftware.easysqllibrary.sql.Column;
import com.mastertechsoftware.easysqllibrary.sql.Database;
import com.mastertechsoftware.logging.Logger;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Table to hold web cookie Information.
 */
public class CookieTable extends AbstractTable<DatabaseCookie> {
    public static final String TABLE_NAME = "cookies";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String COOKIE_COMMENT = "cookieComment";
    public static final String COOKIE_COMMENT_URL = "cookieCommentUrl";
    public static final String COOKIE_DOMAIN = "cookieDomain";
    public static final String COOKIE_EXPIRY_DATE = "cookieExpiryDate";
    public static final String COOKIE_PATH = "cookiePath";
    public static final String IS_SECURE = "isSecure";
    public static final String COOKIE_VERSION = "cookieVersion";
    public static final String LAST_ACCESSED = "last_accessed";

    private static final int ID_COLUMN = 0;
    private static final int NAME_COLUMN = 1;
    private static final int VALUE_COLUMN = 2;
    private static final int COOKIE_COMMENT_COLUMN = 3;
    private static final int COOKIE_COMMENT_URL_COLUMN = 4;
    private static final int COOKIE_DOMAIN_COLUMN = 5;
    private static final int COOKIE_EXPIRY_DATE_COLUMN = 6;
    private static final int COOKIE_PATH_COLUMN = 7;
    private static final int IS_SECURE_COLUMN = 8;
    private static final int COOKIE_VERSION_COLUMN = 9;
    private static final int LAST_ACCESSED_COLUMN = 10;
    private static final int version = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy", Locale.US);
	private CookieMapper mapper;

    public CookieTable() {
        super(TABLE_NAME);
        addColumn(new Column(ID, Column.COLUMN_TYPE.INTEGER, true));
        addColumn(new Column(NAME, Column.COLUMN_TYPE.TEXT));
        addColumn(new Column(VALUE, Column.COLUMN_TYPE.TEXT));
        addColumn(new Column(COOKIE_COMMENT, Column.COLUMN_TYPE.TEXT));
        addColumn(new Column(COOKIE_COMMENT_URL, Column.COLUMN_TYPE.TEXT));
        addColumn(new Column(COOKIE_DOMAIN, Column.COLUMN_TYPE.TEXT));
        addColumn(new Column(COOKIE_EXPIRY_DATE, Column.COLUMN_TYPE.TEXT));
        addColumn(new Column(COOKIE_PATH, Column.COLUMN_TYPE.TEXT));
        addColumn(new Column(IS_SECURE, Column.COLUMN_TYPE.INTEGER));
        addColumn(new Column(COOKIE_VERSION, Column.COLUMN_TYPE.INTEGER));
        addColumn(new Column(LAST_ACCESSED, Column.COLUMN_TYPE.INTEGER));
        setVersion(version);
		mapper = new CookieMapper();
	}

	public CookieMapper getMapper() {
		return mapper;
	}

	@Override
    public DatabaseCookie insertEntry(Database database, DatabaseCookie cookie) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, cookie.getName());
        cv.put(VALUE, cookie.getValue());
        cv.put(COOKIE_COMMENT, cookie.getComment());
        cv.put(COOKIE_COMMENT_URL, cookie.getCommentURL());
        cv.put(COOKIE_DOMAIN, cookie.getDomain());
        Date expiryDate = cookie.getExpiryDate();
        if (expiryDate != null) {
            cv.put(COOKIE_EXPIRY_DATE, dateFormat.format(expiryDate));
        }
        cv.put(COOKIE_PATH, cookie.getPath());
        cv.put(IS_SECURE, cookie.isSecure() ? 1 : 0);
        cv.put(COOKIE_VERSION, cookie.getVersion());
		cv.put(LAST_ACCESSED, cookie.getLastAccessed());
        long id = 0;
        try {
            id = database.getDatabase().insert(TABLE_NAME, NAME, cv);
        } catch (SQLiteException e) {
            Logger.error(this, e.getMessage());
            return cookie;
        }
        cookie.setId(id);
        return cookie;
    }

    @Override
    public int deleteEntry(Database database, Object data) {
        DatabaseCookie cookie = (DatabaseCookie) data;
        String[] whereArgs = new String[1];
        whereArgs[0] = String.valueOf(cookie.getId());
        try {
            return database.getDatabase().delete(TABLE_NAME, ID + "=?", whereArgs);
        } catch (SQLiteException e) {
            Logger.error(this, e.getMessage());
        }
		return 0;
    }

    @Override
    public void deleteAllEntries(Database database) {
        try {
            database.getDatabase().delete(TABLE_NAME, null, null);
        } catch (SQLiteException e) {
            Logger.error(this, e.getMessage());
        }
    }

    @Override
    public DatabaseCookie getEntry(Database database, Object data) {
        String id = (String) data;
        Cursor result;
        String[] params = {
            id
        };
        try {
            result = database.getDatabase().query(TABLE_NAME, projection, NAME + "=?", params,
                    null, null, null);
        } catch (SQLiteException e) {
            Logger.error(this, e.getMessage());
            return null;
        }
        if (result.moveToFirst()) {
            DatabaseCookie cookie = fillCookie(result);
            result.close();
            return cookie;
        }
        result.close();
        return null;
    }

    public List<DatabaseCookie> getAllCookies(Database database) {
        List<DatabaseCookie> databaseCookies = new ArrayList<DatabaseCookie>();
        Cursor result;
        try {
            result = database.getDatabase().query(TABLE_NAME, projection, null, null, null, null,
                    null);
        } catch (SQLiteException e) {
            Logger.error(this, e.getMessage());
            return databaseCookies;
        }
        if (!result.moveToFirst()) {
            result.close();
            return databaseCookies;
        }
        while (!result.isAfterLast()) {
            DatabaseCookie cookie = fillCookie(result);
            databaseCookies.add(cookie);
            result.moveToNext();
        }
        result.close();
        return databaseCookies;
    }

    /**
     * Fill in a SoundboardCookie from the given cursor
     * 
     * @param result
     * @return SoundboardCookie
     */
    public DatabaseCookie fillCookie(Cursor result) {
        String name = result.getString(NAME_COLUMN);
        String value = result.getString(VALUE_COLUMN);
        DatabaseCookie cookie = new DatabaseCookie(name, value);
		cookie.setLastAccessed(result.getLong(LAST_ACCESSED_COLUMN));
        cookie.setId(result.getLong(ID_COLUMN));
        cookie.setComment(result.getString(COOKIE_COMMENT_COLUMN));
        cookie.setCommentURL(result.getString(COOKIE_COMMENT_URL_COLUMN));
        cookie.setDomain(result.getString(COOKIE_DOMAIN_COLUMN));
        try {
            String dateString = result.getString(COOKIE_EXPIRY_DATE_COLUMN);
            if (dateString != null && dateString.length() > 0) {
                cookie.setExpiryDate(dateFormat.parse(dateString));
            }
        } catch (ParseException e) {
            Logger.error(this, "Problems parsing Date");
        }
        cookie.setPath(result.getString(COOKIE_PATH_COLUMN));
        cookie.setSecure(result.getInt(IS_SECURE_COLUMN) == 1);
        cookie.setVersion(result.getInt(COOKIE_VERSION_COLUMN));
        return cookie;
    }

    /**
     * Add all the values from the cookie store to our database. Make sure you
     * delete all values if you don't want duplicates
     * 
     * @param cookieDatabase
     * @param cookieStore
     */
    public void addCookieStore(Database cookieDatabase, CookieStore cookieStore) {
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getValue() != null) {
                DatabaseCookie databaseCookie = new DatabaseCookie(cookie.getName(),
                        cookie.getValue());
                databaseCookie.fillCookie(cookie);
				databaseCookie.setLastAccessed(System.currentTimeMillis());
                insertEntry(cookieDatabase, databaseCookie);
            } else {
                Logger.error(this, "addCookieStore. Not adding cookie " + cookie.getName()
                        + " because of null value");
            }
        }
    }

    /**
     * Get a cookie store from our database.
     * 
     * @param cookieDatabase
     * @return CookieStore
     */
    public CookieStore getCookieStore(Database cookieDatabase) {
        CookieStore store = new BasicCookieStore();
        List<DatabaseCookie> soundboardCookies = getAllCookies(cookieDatabase);
        for (DatabaseCookie soundboardCookie : soundboardCookies) {
            store.addCookie(soundboardCookie);
        }
        return store;
    }

	/**
	 * This probably won't work since name is private and has to be added to the constructor
	 */
	public class CookieMapper extends AbstractDataMapper<DatabaseCookie> {

		@Override
		public void write(ContentValues cv, Column column, DatabaseCookie databaseCookie) {
			switch (column.getColumnPosition()) {
				case NAME_COLUMN:
					if (databaseCookie.getName() != null) {
						cv.put(NAME, databaseCookie.getName());
					}
					break;
				case VALUE_COLUMN:
					cv.put(VALUE, databaseCookie.getValue());
					break;
				case COOKIE_COMMENT_COLUMN:
					cv.put(COOKIE_COMMENT, databaseCookie.getComment());
					break;
				case COOKIE_COMMENT_URL_COLUMN:
					cv.put(COOKIE_COMMENT_URL, databaseCookie.getCommentURL());
					break;
				case COOKIE_DOMAIN_COLUMN:
					cv.put(COOKIE_DOMAIN, databaseCookie.getDomain());
					break;
				case COOKIE_EXPIRY_DATE_COLUMN:
					cv.put(COOKIE_EXPIRY_DATE, databaseCookie.getExpiryDate().getTime());
					break;
				case COOKIE_PATH_COLUMN:
					cv.put(COOKIE_PATH, databaseCookie.getPath());
					break;
				case IS_SECURE_COLUMN:
					cv.put(IS_SECURE, databaseCookie.isSecure() ? 1 : 0);
					break;
				case COOKIE_VERSION_COLUMN:
					cv.put(COOKIE_VERSION, databaseCookie.getVersion());
					break;
				case LAST_ACCESSED_COLUMN:
					cv.put(LAST_ACCESSED, databaseCookie.getLastAccessed());
					break;
			}
		}

		@Override
		public void read(Cursor cursor, Column column, DatabaseCookie databaseCookie) {
			int columnIndex = getColumnIndex(cursor, column.getName());
			if (columnIndex == -1) {
				Logger.error(this, "CookieMapper.read: Column " + column.getName() + " does not exist in cursor");
				return;
			}
			switch (column.getColumnPosition()) {
				case ID_COLUMN:
					databaseCookie.setId(cursor.getLong(columnIndex));
					break;
				case NAME_COLUMN:
					break;
				case VALUE_COLUMN:
					databaseCookie.setValue(cursor.getString(columnIndex));
					break;
				case COOKIE_COMMENT_COLUMN:
					databaseCookie.setComment(cursor.getString(columnIndex));
					break;
				case COOKIE_COMMENT_URL_COLUMN:
					databaseCookie.setCommentURL(cursor.getString(columnIndex));
					break;
				case COOKIE_DOMAIN_COLUMN:
					databaseCookie.setDomain(cursor.getString(columnIndex));
					break;
				case COOKIE_PATH_COLUMN:
					databaseCookie.setPath(cursor.getString(columnIndex));
					break;
				case IS_SECURE_COLUMN:
					databaseCookie.setSecure(cursor.getInt(columnIndex) == 1);
					break;
				case COOKIE_VERSION_COLUMN:
					databaseCookie.setVersion(cursor.getInt(columnIndex));
					break;
				case LAST_ACCESSED_COLUMN:
					databaseCookie.setLastAccessed(cursor.getLong(columnIndex));
					break;
			}
		}
	}
}
