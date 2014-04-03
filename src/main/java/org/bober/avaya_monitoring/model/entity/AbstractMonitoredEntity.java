package org.bober.avaya_monitoring.model.entity;

/**
 * Abstract class for all entity-classes which can be monitored in the tasks
 */
public abstract class AbstractMonitoredEntity extends AbstractEntity {
    private String name;
    private boolean deleted;
    private  String description;

    protected AbstractMonitoredEntity() {
    }

    public AbstractMonitoredEntity(String name){
        setName( name );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * return prepared entity name for statistic
     * for example : 'kv-aic-01(10.7.2.111)'
     */
    public abstract String getPrepareName();
}
