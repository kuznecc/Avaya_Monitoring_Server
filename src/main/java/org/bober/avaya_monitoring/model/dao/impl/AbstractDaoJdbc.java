package org.bober.avaya_monitoring.model.dao.impl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

/** This is abstract class for all dao-classes.
 * When you use any child dao-class you must set dbTable-property
 */
public abstract class AbstractDaoJdbc extends NamedParameterJdbcDaoSupport {

    /**
     * Name of table in the DB, that will be used in this DAO
     */
    private String dbTableName;

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public String getDbTableName() {
        return dbTableName;
    }

}
