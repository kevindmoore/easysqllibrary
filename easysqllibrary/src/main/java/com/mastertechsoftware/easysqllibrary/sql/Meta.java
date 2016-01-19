package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Holds info about a database version. creationString is used for recreating that database
 */
public class Meta extends DefaultReflectTable {
    protected int version;
    protected String database;
    protected String creationString;
}
