package org.bober.avaya_monitoring.model.dao;


import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;

import java.util.Map;

/**
 * Common interface for all AbstractMonitoredEntity dao. It can work with child entity's.
 * @param <T> - child of AbstractMonitored entity (for example Server, AvayaParameter)
 */
public interface iMonitoredEntityDao<T extends AbstractMonitoredEntity> extends iAbstractDao<T> {

    public T get(String name);

    public Map<Integer, T > getEntityMap();

    /* return class of entity, which can be obtained by this dao */
    public Class<T> getMonitoredEntityClass();

    }
