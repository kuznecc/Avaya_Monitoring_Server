package org.bober.avaya_monitoring.model.dao;


import org.bober.avaya_monitoring.model.entity.AbstractEntity;

import java.util.List;

/**
 * Common interface for all dao
 */
public interface iAbstractDao<T extends AbstractEntity> {

    /**
     * Set & Get table name of current dao
     */

    void setDbTableName(String dbTableName);
    String getDbTableName();


    T get(int id);

    List<T> getAll();

    boolean create(T newInstance);

    boolean create(List<T> instancesList);

    boolean delete(T instance);

    boolean update(T newInstance);

}
