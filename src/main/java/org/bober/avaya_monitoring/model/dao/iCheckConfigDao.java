package org.bober.avaya_monitoring.model.dao;


import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.entity.CheckConfig;

/**
 * Common interface for all CheckConfig tables dao
 */
public interface iCheckConfigDao extends iAbstractDao<CheckConfig> {

    /* Set dao that can return monitored entities, that will be checked in the task */
    public void setMonitoredEntityDao(iMonitoredEntityDao<AbstractMonitoredEntity> monitoredEntityDao);

}
