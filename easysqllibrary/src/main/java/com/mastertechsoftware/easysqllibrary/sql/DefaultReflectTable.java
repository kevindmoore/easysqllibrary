package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Default Class that implements this simple interface
 */
public class DefaultReflectTable implements ReflectTableInterface {
    // These HAVE to BE Ints
    protected int _id;

    @Override
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }
}
