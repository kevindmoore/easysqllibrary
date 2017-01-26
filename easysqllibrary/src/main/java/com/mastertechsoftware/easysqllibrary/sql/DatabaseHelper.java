package com.mastertechsoftware.easysqllibrary.sql;

import java.util.List;

/**
 * Helper Class that does the regular CRUD operations
 */
public class DatabaseHelper {
    protected String databaseName;

	/**
     * Constructor
     * @param databaseName
     */
    public DatabaseHelper(String databaseName) {
        this.databaseName = databaseName;
    }

	/**
     * Create the database given a list of classes that implement ReflectTableInterface
     * This should be done 1x in the Application class
     * @param mainTableName
     * @param tableInterfaces
     */
    public void createDatabase(String mainTableName, Class<? extends ReflectTableInterface>... tableInterfaces) {
        DatabaseManager.getInstance().addDatabase(databaseName, mainTableName, tableInterfaces);

    }

	/**
     * Delete the entire database - CAUTION
     */
    public void deleteDatabase() {
        DatabaseManager.getInstance().deleteDatabase(databaseName);
    }

	/**
     * Get a single table item
     * @param tableClass
     * @param id
     * @return table item
     */
    public ReflectTableInterface get(Class<? extends ReflectTableInterface> tableClass, int id) {
        return (ReflectTableInterface) DatabaseManager.getInstance().getItem(databaseName, tableClass, id);
    }

	/**
	 *
	 * @param tableClass
	 * @param columnName - name of column to search
	 * @param columnValue - value of column to search
	 * @return list of items
	 */
    public List<? extends ReflectTableInterface> getWhere(Class<? extends ReflectTableInterface> tableClass, String columnName, String columnValue) {
        return (List<? extends ReflectTableInterface>) DatabaseManager.getInstance().getItemsWhere(databaseName, tableClass, columnName, columnValue);
    }

	/**
     * Return a list of all the table items
     * @param tableClass
     * @return List of Table
     */
    public List<? extends ReflectTableInterface> getAll(Class<? extends ReflectTableInterface> tableClass) {
        return  (List<ReflectTableInterface>)DatabaseManager.getInstance().getAllItems(databaseName, tableClass);
    }

	/**
     * Add a single table
     * @param tableClass
     * @param table
     * @return id
     */
    public long add(Class<? extends ReflectTableInterface> tableClass, ReflectTableInterface table) {
        return DatabaseManager.getInstance().addItem(databaseName, tableClass, table);
    }

	/**
     * Add a list of items
     * @param tableClass
     * @param tables
     * @return true if any insertion failed
     */
    public boolean addAll(Class<? extends ReflectTableInterface> tableClass, List<? extends ReflectTableInterface> tables) {
        boolean failed = false;
        for (ReflectTableInterface table : tables) {
            if (DatabaseManager.getInstance().addItem(databaseName, tableClass, table) == -1) {
                failed = true;
            }
        }
        return failed;
    }

	/**
     * Update a single table
     * @param tableClass
     * @param table
     */
    public void update(Class<? extends ReflectTableInterface> tableClass, ReflectTableInterface table) {
        DatabaseManager.getInstance().updateItem(databaseName, tableClass, table);
    }

	/**
     * Remove all the items for a class
     * @param tableClass
     */
    public void removeAll(Class<? extends ReflectTableInterface> tableClass) {
        DatabaseManager.getInstance().removeAllItems(databaseName, tableClass);
    }

	/**
     * Delete a single table item
     * @param tableClass
     * @param table
     */
    public void delete(Class<? extends ReflectTableInterface> tableClass, ReflectTableInterface table) {
        DatabaseManager.getInstance().deleteItem(databaseName, tableClass, table.getId());

    }
}
