package com.mastertechsoftware.easysqllibrary.sql;

/**
 * Class used to describe a SQL Column and it's type
 */
public class Column {
	public enum COLUMN_TYPE {
		INTEGER,
        LONG,
        DOUBLE,
        TEXT,
        FLOAT,
        BOOLEAN,
        TIMESTAMP,
        BLOB;

        public static COLUMN_TYPE getType(String type) {
            if (INTEGER.toString().equalsIgnoreCase(type)) {
                return INTEGER;
            } else if (LONG.toString().equalsIgnoreCase(type)) {
                return LONG;
            } else if (DOUBLE.toString().equalsIgnoreCase(type)) {
                return DOUBLE;
            } else if (TEXT.toString().equalsIgnoreCase(type)) {
                return TEXT;
            } else if (FLOAT.toString().equalsIgnoreCase(type)) {
                return FLOAT;
            } else if (BOOLEAN.toString().equalsIgnoreCase(type)) {
                return BOOLEAN;
            } else if (TIMESTAMP.toString().equalsIgnoreCase(type)) {
                return TIMESTAMP;
            } else if (BLOB.toString().equalsIgnoreCase(type)) {
                return BLOB;
            }
            return TEXT;
        }
    }

	protected String name;
	protected COLUMN_TYPE type;
	protected boolean key = false;
    protected boolean notNull = false;
    protected boolean unique = false;
	protected int column_position;

  /**
   * Constructor
   */
	public Column() {
	}

  /**
   * Constructor
   * @param name
   * @param type
   */
	public Column(String name, COLUMN_TYPE type) {
		this.name = name;
		this.type = type;
	}

  /**
   * Constructor
   * @param name
   * @param type
   * @param key
   */
	public Column(String name, COLUMN_TYPE type, boolean key) {
		this.key = key;
		this.name = name;
		this.type = type;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public COLUMN_TYPE getType() {
		return type;
	}

	public void setType(COLUMN_TYPE type) {
		this.type = type;
	}

  /**
   * Get the create string needed for this column
   * @return SQL String
   */
	public String getCreateString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append(" ").append(type.toString()).append(" ");
		if (key) {
			builder.append(" PRIMARY KEY AUTOINCREMENT ");
		}
        if (notNull) {
            builder.append(" NOT NULL ");
        }
        if (unique) {
            builder.append(" UNIQUE ");
        }
		return builder.toString();

	}

	public int getColumnPosition() {
		return column_position;
	}

	public void setColumnPosition(int column_position) {
		this.column_position = column_position;
	}

    @Override
    public String toString() {
        return name;
    }
}
