package org.bober.avaya_monitoring.model.entity;

import java.util.Date;

/**
 * This class describe an result of monitoring task.
 * This instance is a one row of table servers_cpu_load.
 */
public class CheckResult extends AbstractEntity {

    /* values from table */
    private int entityId;
    private String attributes;
    private Date date;
    private Integer value;

    /**
     * Entity object, that has been checked
     */
    private AbstractMonitoredEntity entity;

    public CheckResult() {}

    public CheckResult(Date date, int entityId, Integer value) {
        this.entityId = entityId;
        this.date = date;
        this.value = value;
    }

    public CheckResult(Date date, int entityId, String attributes, Integer value) {
        this.entityId = entityId;
        this.attributes = attributes;
        this.date = date;
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public CheckResult setDate(Date date) {
        this.date = date;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public CheckResult setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getAttributes() {
        return attributes;
    }

    public CheckResult setAttributes(String attributes) {
        this.attributes = attributes;
        return this;
    }

    public AbstractMonitoredEntity getEntity() {
        return entity;
    }

    public CheckResult setEntity(AbstractMonitoredEntity entity) {
        this.entity = entity;
        return this;
    }

    public Integer getValue() {
        return value;
    }

    public CheckResult setValue(Integer value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "id=" + getId() +
                ", attributes='" + attributes + "'" +
                ", date=" + date +
                ", entityId=" + entityId +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckResult that = (CheckResult) o;

        if (entityId != that.entityId) return false;
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityId;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
