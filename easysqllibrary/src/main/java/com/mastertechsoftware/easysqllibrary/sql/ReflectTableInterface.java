package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Interface for all classes that need to be read by reflection. Only need to implement this interface
 * and have a long _id field.
 */
public interface ReflectTableInterface {
	// These HAVE to BE Ints
	int getId();
	void setId(int id);
}
