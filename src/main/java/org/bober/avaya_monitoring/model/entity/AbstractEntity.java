package org.bober.avaya_monitoring.model.entity;

/**
 * Abstract class for all entity that can be stored in DB
 */
public abstract class AbstractEntity {

    /**
     * Name of table in the DB that consist this entity
     */
    private String dbTableName;

    /**
     * ID of this entity which equal primary key in the db table
     */
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public abstract String toString();
}
