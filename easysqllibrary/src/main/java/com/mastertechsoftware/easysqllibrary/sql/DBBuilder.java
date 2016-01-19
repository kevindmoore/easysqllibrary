package com.mastertechsoftware.easysqllibrary.sql;

import com.mastertechsoftware.logging.Logger;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Class to dynamically build a database
 */
public class DBBuilder {
    protected String mainTableName;
    protected String databaseName;
    protected int version = 1;
    protected BaseDatabaseHelper baseDatabaseHelper;
    protected List<Table> tables = new ArrayList<Table>();
    protected Map<String, TableItem> tableItems = new HashMap<String, TableItem>();

    protected Context context;

    public DBBuilder(Context context) {
        this.context = context;
    }

    /**
     * Set the main table name. This is used to test the existence of the database
     * @param mainTableName
     */
    public void setMainTableName(String mainTableName) {
        this.mainTableName = mainTableName;
    }

    /**
     * Set the database name. This is the name of the file that is stored in /data/app
     * @param databaseName
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Set the version. Defaults to 1.
     * @param version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Add a table by itself. Add columns later.
     * @param tableName
     */
    public Table addTable(String tableName) {
        Table table = new AbstractTable(tableName);
        tables.add(table);
        tableItems.put(tableName, new TableItem(table, tableName));
        table.addColumn(new Column("_id", Column.COLUMN_TYPE.INTEGER, true));
        return table;
    }

    /**
     * Add a table with columns.
     * @param tableName
     * @param columnNames
     */
    public void addTable(String tableName, List<String> columnNames) {
        Table table = addTable(tableName);
        for (String columnName : columnNames) {
            Column column = new Column(columnName, Column.COLUMN_TYPE.TEXT);
            table.addColumn(column);
        }
    }

    /**
     * Add a column to a table
     * @param tableName
     * @param columnName
     */
    public void addTableColumn(String tableName, String columnName) {
        TableItem tableItem = tableItems.get(tableName);
        Table table = null;
        if (tableItem == null) {
            table = addTable(tableName);
        } else {
            table = tableItem.table;
        }
        Column column = new Column(columnName, Column.COLUMN_TYPE.TEXT);
        table.addColumn(column);
    }

    /**
     * Return the Table entry for the given table name.
     * @param tableName
     * @return
     */
    public TableEntry getTableEntry(String tableName) {
        return tableItems.get(tableName);
    }

    /**
     * Call this method after setting up the table and column information.
     * @return BaseDatabaseHelper
     * @throws DBException
     */
    public BaseDatabaseHelper build() throws DBException {
        if (databaseName == null || mainTableName == null || tables.size() == 0) {
            throw new DBException("Database, table name & tables must be set before building");
        }
        baseDatabaseHelper = new DBBuilderBaseDatabaseHelper(context, databaseName, mainTableName, version);
        return baseDatabaseHelper;
    }

    /**
     * Subclass of BaseDatabaseHelper. Creates a database with our tables.
     */
    class DBBuilderBaseDatabaseHelper extends BaseDatabaseHelper {

        protected DBBuilderBaseDatabaseHelper(Context context, String databaseName, String mainTableName, int version) {
            super(context, databaseName, mainTableName, version);
        }

        @Override
        protected void createLocalDB() {
            localDatabase = new Database(sqLiteDatabase);
            localDatabase.setTables(tables);
        }
    }

    public static void testDBuilder(Context context) {
        DBBuilder builder = new DBBuilder(context);
        builder.setDatabaseName("testing.db");
        String testingTable = "testingTable";
        builder.setMainTableName(testingTable);
        List<String> columns = new ArrayList<String>();
        columns.add("col1");
        columns.add("col2");
        columns.add("col3");
        columns.add("col4");
        builder.addTable(testingTable, columns);
        try {
            BaseDatabaseHelper buildHelper = builder.build();
            buildHelper.dropDatabase();
            List<String> data = new ArrayList<String>();
            data.add("data1-1");
            data.add("data1-2");
            data.add("data1-3");
            data.add("data1-4");
            long row1Id = buildHelper.insertEntry(builder.getTableEntry(testingTable), data);
            Logger.debug(context, "Id is " + row1Id);
            data.clear();
            data.add("data2-1");
            data.add("data2-2");
            data.add("data2-3");
            data.add("data2-4");
            long row2Id = buildHelper.insertEntry(builder.getTableEntry(testingTable), data);
            Cursor allEntries = buildHelper.getAllEntries(
                    builder.getTableEntry(testingTable));
            Logger.debug(context, "After inserting row " + row2Id);
            if (allEntries != null) {
                if (allEntries.moveToFirst()) {
                    printCursor(context, allEntries);
                }
                allEntries.close();
            }
            Cursor cursor = buildHelper.getEntry(builder.getTableEntry(testingTable), row1Id);
            Logger.debug(context, "Searching for row " + row1Id);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        printCursor(context, cursor);
                    }
                } finally {
                    cursor.close();
                }
            }
            cursor = buildHelper.getEntry(builder.getTableEntry(testingTable), "col2", "data1-2");
            Logger.debug(context, "Searching for data1-2" );
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        printCursor(context, cursor);
                    }
                } finally {
                    cursor.close();
                }
            }
            buildHelper.deleteEntry(builder.getTableEntry(testingTable), row1Id);
            Logger.debug(context, "After deleting row " + row1Id);
            allEntries = buildHelper.getAllEntries(
                    builder.getTableEntry(testingTable));
            if (allEntries != null) {
                if (allEntries.moveToFirst()) {
                    printCursor(context, allEntries);
                }
                allEntries.close();
            }
            data.clear();
            data.add(String.valueOf(row2Id));
            data.add("data2-2-1");
            data.add("data2-2-2");
            data.add("data2-2-3");
            data.add("data2-2-4");
            buildHelper.updateEntry(builder.getTableEntry(testingTable), data, String.valueOf(row2Id));
            cursor = buildHelper.getEntry(builder.getTableEntry(testingTable), row2Id);
            Logger.debug(context, "After updating row " + row2Id);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        printCursor(context, cursor);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (DBException e) {
            e.printStackTrace();
        }

    }

    private static void printCursor(Context context, Cursor cursor) {
        int count = cursor.getCount();
        Logger.debug(context, "Found " + count + " items");
        String[] columnNames = cursor.getColumnNames();
        for (String columnName : columnNames) {
            Logger.debug(context, "Column: " + columnName);
        }
        for (int i=0; i < count; i++) {
            for (int j=0; j < columnNames.length; j++) {
                int columnIndex = cursor.getColumnIndex(columnNames[j]);
                if (columnIndex != -1) {
                    Logger.debug(context, "Data for column: " + j + " is " + cursor.getString(columnIndex));
                } else {
                    Logger.debug(context, "Data for column: " + columnNames[j] + " not found ");
                }
            }
            cursor.moveToNext();
        }
    }
}
