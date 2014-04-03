package org.bober.avaya_monitoring.model.entity;

/**
 * Abstract class for all entity that can be stored in DB
 */
public abstract class AbstractEntity {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract String toString();
}
