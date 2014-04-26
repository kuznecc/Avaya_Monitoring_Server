package org.bober.avaya_monitoring.model.entity;

/**
 * This class describe an configuration of monitoring task.
 * This instance is a one row of table servers_cpu_load_check_cfg.
 */
public class CheckConfig extends AbstractEntity {

    private int entityId;
    private String attributes;
    private int frequency;
    private boolean disabled;
    private  String description;

    /**
     * Entity object, that has been checked in the configured task
     */
    private AbstractMonitoredEntity entity;


    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public AbstractMonitoredEntity getEntity() {
        return entity;
    }

    public void setEntity(AbstractMonitoredEntity entity) {
        this.entity = entity;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // CheckConfig{id=2, entityId=5, attributes='98200', frequency=1800000, disabled=false, description='0800506800'}
    @Override
    public String toString() {
        return "CheckConfig{" +
                "id=" + getId() +
                ", entityId=" + entityId + ((entity == null) ? "" : "(" + entity.getName() + ")") +
                ", attributes='" + attributes + "'" +
                ", frequency=" + frequency +
                ", disabled=" + disabled +
                ", description='" + description + "'" +
                '}';
    }
}
