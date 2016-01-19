package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Generic holder for Table Entries
 */
public class TableItem implements TableEntry {
    protected Table table;
    protected String tableName;

    public TableItem(Table table, String tableName) {
        this.table = table;
        this.tableName = tableName;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public String getName() {
        return tableName;
    }

}
