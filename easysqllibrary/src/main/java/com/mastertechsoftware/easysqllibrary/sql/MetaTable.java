package com.mastertechsoftware.easysqllibrary.sql;

/**
 *  Hold Meta info on database tables
 */
public class MetaTable extends ReflectTable<Meta> {

    public MetaTable(Meta type, Database database) {
        super(type, database);
    }
}
