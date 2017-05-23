package com.mastertechsoftware.easysqllibrary.sql;

import com.mastertechsoftware.easysqllibrary.reflect.UtilReflector;
import com.mastertechsoftware.logging.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * Table that is built by using reflection on the object
 */
public class ReflectTable<T> extends AbstractTable<T> {
	private Mapper<T> mapper;
    private T type;
    private Database database;

	public ReflectTable(T type, Database database) {
        this.type = type;
        this.database = database;
		mapper = new Mapper();
        this.mapper.setDatabase(database);
		String tableName = type.getClass().getSimpleName().toLowerCase();
		setTableName(tableName);
        readProperties(type);
	}

    private void readProperties(T type) {
        ArrayList<Field> allFields = UtilReflector.getAllFields(type.getClass());
		allFields = UtilReflector.removeTransient(allFields);
        boolean idFieldFound = false;
        for (Field field : allFields) {
            Column.COLUMN_TYPE column_type;
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            if (fieldType == int.class || fieldType == Integer.class || fieldType == Short.class) {
                column_type = Column.COLUMN_TYPE.INTEGER;
            } else if (fieldType == float.class || fieldType == Float.class) {
                column_type = Column.COLUMN_TYPE.FLOAT;
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                column_type = Column.COLUMN_TYPE.BOOLEAN;
            } else if (fieldType == long.class || fieldType == Long.class) {
                column_type = Column.COLUMN_TYPE.LONG;
            } else if (fieldType == double.class || fieldType == Double.class) {
                column_type = Column.COLUMN_TYPE.DOUBLE;
            } else if (fieldType == Number.class) {
                column_type = Column.COLUMN_TYPE.INTEGER;
            } else if (fieldType == String.class || fieldType == Character.class) {
                column_type = Column.COLUMN_TYPE.TEXT;
            } else if (fieldType == Object.class) {
                throw new IllegalArgumentException("ReflectTable does not support Objects. Please use basic field types");
            } else {
                if (UtilReflector.hasInterface(fieldType, ReflectTableInterface.class)) {
                    Column column = new Column(fieldName + "_id", Column.COLUMN_TYPE.INTEGER, false);
                    addColumn(column);
                } else {
                    Logger.debug("Unhandled type " + fieldType + ". Please use basic field types");
                }
                continue;
            }
            Column column = null;
            if (ID.equalsIgnoreCase(fieldName)) {
                idFieldFound = true;
                column = new Column(fieldName, column_type, true);
            } else {
                column = new Column(fieldName, column_type);
            }
            addColumn(column);
        }
        if (!idFieldFound) {
            throw new RuntimeException("No ID field found for table " + getTableName());
        }
    }

    public List<Field> getReflectFields() {
        List<Field> reflectFields = new ArrayList<Field>();
        ArrayList<Field> allFields = UtilReflector.getAllFields(type.getClass());
		allFields = UtilReflector.removeTransient(allFields);
        for (Field field : allFields) {
            Class<?> fieldType = field.getType();
            if (UtilReflector.hasInterface(fieldType, ReflectTableInterface.class)) {
                reflectFields.add(field);
            }
        }
        return reflectFields;
    }

    public Mapper<T> getMapper() {
		return mapper;
	}

    /**
     * Insert a new entry into the db using a mapper
     * @param database
     * @param data
     * @param mapper
     * @return new id
     */
    public int insertEntry(Database database, T data, DataMapper<T> mapper) throws DBException {
        if (data == null) {
            Logger.error("insertEntry data is null");
            return -1;
        }
        if (UtilReflector.isBasicType(data.getClass())) {
            return super.insertEntry(database, data, mapper);
        }
        ArrayList<Field> allFields = UtilReflector.getAllFields(type.getClass());
		allFields = UtilReflector.removeTransient(allFields);
        int columnPosition = 0;
        ContentValues cv = new ContentValues();
        for (Column column : columns) {
            Field field = AbstractDataMapper.getColumnField(allFields, column);
            if (field == null) {
                continue;
            }
            if (column.column_position == 0) {
                column.column_position = columnPosition;
            }
            mapper.write(cv, column, data);
            columnPosition++;
        }
        List<Field> reflectfields = getReflectFields();
        for (Field reflectfield : reflectfields) {
            ReflectTable subTable = (ReflectTable) database.getTable(reflectfield.getType().getSimpleName());
            T fieldData = (T) UtilReflector.getField(data, reflectfield.getName());
            if (fieldData != null) {
                long id = subTable.insertEntry(database, fieldData, subTable.getMapper());
                if (id != -1) {
                    String fieldName = reflectfield.getName() + "_id";
                    cv.put(fieldName, id);
                    Column column = getColumn(fieldName);
                    mapper.write(cv, column, fieldData);
                } else {
                    Logger.error("Problems inserting " + subTable.getTableName());
                }
            }
        }
        return super.insertEntry(database, cv);
    }

    @Override
    public void deleteAllEntries(Database database) throws DBException {
        List<? extends T> allEntries = getAllEntries(database, (Class<? extends T>) type.getClass(), getMapper());
        List<Field> reflectfields = getReflectFields();
        for (Field reflectfield : reflectfields) {
            ReflectTable subTable = (ReflectTable) database.getTable(reflectfield.getType().getSimpleName());
            reflectfield.setAccessible(true);
            for (T allEntry : allEntries) {
                T fieldData = (T) UtilReflector.getField(allEntry, reflectfield.getName());
                if (fieldData != null) {
                    String[] whereArgs = new String[1];
                    whereArgs[0] = String.valueOf(((ReflectTableInterface) fieldData).getId());
                    try {
                        database.getDatabase().delete(subTable.getTableName(), "_id=?", whereArgs);
                    } catch (SQLiteException e) {
                        Logger.error(this, e.getMessage());
                    }
                }
            }
        }
        super.deleteAllEntries(database);
    }


    @Override
    public int deleteEntryWhere(Database database, String whereClause, String[] whereArgs) throws DBException {
        List<? extends T> allEntries = getAllEntriesWhere(database, whereClause, whereArgs, (Class<T>) type.getClass(), getMapper());
        List<Field> reflectfields = getReflectFields();
        for (Field reflectfield : reflectfields) {
            ReflectTable subTable = (ReflectTable) database.getTable(reflectfield.getType().getSimpleName());
            reflectfield.setAccessible(true);
            for (T allEntry : allEntries) {
                T fieldData = (T) UtilReflector.getField(allEntry, reflectfield.getName());
                if (fieldData != null) {
                    String[] subWhereArgs = new String[1];
                    subWhereArgs[0] = String.valueOf(((ReflectTableInterface) fieldData).getId());
                    try {
                        database.getDatabase().delete(subTable.getTableName(), "_id=?", subWhereArgs);
                    } catch (SQLiteException e) {
                        Logger.error(this, e.getMessage());
                    }
                }
            }
        }
        return super.deleteEntryWhere(database, whereClause, whereArgs);
    }

    public int deleteEntryWhere(Database database, String columnName, String columnValue) throws DBException {
        List<? extends T> allEntries = getAllEntriesWhere(database, (Class<T>) type.getClass(), columnName, columnValue, getMapper());
        List<Field> reflectfields = getReflectFields();
        for (Field reflectfield : reflectfields) {
            ReflectTable subTable = (ReflectTable) database.getTable(reflectfield.getType().getSimpleName());
            reflectfield.setAccessible(true);
            for (T allEntry : allEntries) {
                T fieldData = (T) UtilReflector.getField(allEntry, reflectfield.getName());
                if (fieldData != null) {
                    String[] subWhereArgs = new String[1];
                    subWhereArgs[0] = String.valueOf(((ReflectTableInterface) fieldData).getId());
                    try {
						database.getDatabase().delete(subTable.getTableName(), "_id=?", subWhereArgs);
                    } catch (SQLiteException e) {
                        Logger.error(this, e.getMessage());
                    }
                }
            }
        }
        return super.deleteEntryWhere(database, columnName, columnValue);

    }


    /**
     * Mapper class. Get all fields and use the column to get it's type
     */
	public class Mapper<T> extends AbstractDataMapper<T> {
        Database database;
		private ArrayList<Field> allFields;
		private List<Field> reflectFields;
		private boolean recurse = true;

		public void setDatabase(Database database) {
            this.database = database;
        }

		public void setRecurse(boolean recurse) {
			this.recurse = recurse;
		}

		@Override
		public void write(ContentValues cv, Column column, T type) {
			if (allFields == null) {
				allFields = UtilReflector.getAllFields(type.getClass());
				allFields = UtilReflector.removeTransient(allFields);
			}
			if (reflectFields == null) {
				reflectFields = getReflectFields();
			}
            Field field = getColumnField(allFields, column);
            if (field == null) {
				field = getReflectColumnField(reflectFields, column);
                if (field != null) {
                    cv.put(column.getName(), (Long) cv.get(column.getName()));
					return;
                }
                Logger.error(this, "Field at position " + column.getColumnPosition() + " does not exist");
                return;
            }
			field.setAccessible(true);

			// Need to skip ID
			if (column.getName().equalsIgnoreCase(ID)) {
				return;
			}
            if (!isValidType(field.getType())) {
                Logger.debug("Invalid object of type " + field.getType());
                return;
            }
			switch (column.getType()) {
				case TEXT:
					try {
						cv.put(column.getName(), (String)field.get(type));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case INTEGER:
					try {
						cv.put(column.getName(), (Integer)field.get(type));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case FLOAT:
					try {
						cv.put(column.getName(), (Float)field.get(type));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case BOOLEAN:
					try {
						cv.put(column.getName(), (Boolean)field.get(type));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case LONG:
					try {
						cv.put(column.getName(), (Long)field.get(type));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case DOUBLE:
					try {
						cv.put(column.getName(), (Double)field.get(type));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
			}
		}

		@Override
		public void read(Cursor cursor, Column column, T type) throws DBException {
			int columnIndex = getColumnIndex(cursor, column.getName());
			if (columnIndex == -1) {
				Logger.error(this, "Mapper.read: Column " + column.getName() + " does not exist in cursor");
				return;
			}
			if (allFields == null) {
				allFields = UtilReflector.getAllFields(type.getClass());
				allFields = UtilReflector.removeTransient(allFields);
			}
			if (reflectFields == null) {
				reflectFields = getReflectFields();
			}
			Field field = getColumnField(allFields, column);
            if (field == null) {
                field = getReflectColumnField(reflectFields, column);
                if (field != null) {
                    field.setAccessible(true);
                    long id = cursor.getLong(columnIndex);
                    if (database != null) {
                        ReflectTable subTable = (ReflectTable) database.getTable(field.getType().getSimpleName());
                        try {
							Mapper subTableMapper = (Mapper)subTable.getMapper();
							subTableMapper.setRecurse(false);
							Object data = subTable.getEntry(database, id, field.getType().newInstance(), subTableMapper);
                            field.set(type, data);
                        } catch (IllegalAccessException e) {
                            Logger.error(this, "Problems mapping column " + column.getName(), e);
                        } catch (InstantiationException e) {
                            Logger.error(this, "Problems mapping column " + column.getName(), e);
                        }

                    }
                    return;
                }
                Logger.error(this, "Field at position " + column.getColumnPosition() + " does not exist");
                return;
			}
			field.setAccessible(true);
            if (!isValidType(field.getType())) {
                Logger.debug("Invalid object of type " + field.getType());
                return;
            }
			switch (column.getType()) {
				case TEXT:
					try {
						field.set(type, cursor.getString(columnIndex));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case INTEGER:
					try {
						field.set(type, cursor.getInt(columnIndex));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case FLOAT:
					try {
						field.set(type, cursor.getFloat(columnIndex));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case BOOLEAN:
					try {
						field.set(type, cursor.getInt(columnIndex) == 1 ? Boolean.TRUE : Boolean.FALSE);
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case LONG:
					try {
						field.set(type, cursor.getLong(columnIndex));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
				case DOUBLE:
					try {
						field.set(type, cursor.getDouble(columnIndex));
					} catch (IllegalAccessException e) {
						Logger.error(this, "Problems mapping column " + column.getName(), e);
					}
					break;
			}
		}

        protected Field getReflectColumnField(List<Field> allFields, Column column) {
            for (Field allField : allFields) {
                String fieldName = allField.getName() + "_id";
                if (fieldName.equalsIgnoreCase(column.getName())) {
                    return allField;
                }
            }
            return null;
        }
	}

}
