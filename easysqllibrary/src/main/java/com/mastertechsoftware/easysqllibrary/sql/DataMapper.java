package com.mastertechsoftware.easysqllibrary.sql;

import android.content.ContentValues;
import android.database.Cursor;
/**
 * Interface for mapping a class T with a Column
 */
public interface DataMapper<T> {
    void write(ContentValues cv, Column column, T data);
    void read(Cursor cursor, Column column, T data) throws DBException;
}
