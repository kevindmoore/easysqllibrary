package com.mastertechsoftware.easysqllibrary.sql.upgrade;

import com.mastertechsoftware.easysqllibrary.sql.ClassField;
/**
 * Interface for mapping a class T with a Column
 */
public interface UpgradeDataMaper<T> {
    void write(UpgradeHolder upgradeHolder, ClassField column, T data);
}
