package org.bober.avaya_monitoring.model.dao.impl;

import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.helper.ExtendedBeanPropertyRowMapper;
import org.bober.avaya_monitoring.model.dao.iCheckConfigDao;
import org.bober.avaya_monitoring.model.entity.CheckConfig;

import java.util.*;

/**
 * Dao-class for access to all db tables, that consist fields for CheckConfig entity.
 */
public class ConfigTableDaoJdbc extends AbstractDaoJdbc
        implements iCheckConfigDao {

    /* Dao that can return monitored entities, that will be checked in the task */
    private iMonitoredEntityDao<AbstractMonitoredEntity> monitoredEntityDao;

    @Override
    public void setMonitoredEntityDao(iMonitoredEntityDao<AbstractMonitoredEntity> monitoredEntityDao) {
        this.monitoredEntityDao = monitoredEntityDao;
    }



        @Override
    public CheckConfig get(int id) {
            String sql = "SELECT * FROM " + getDbTableName() + " WHERE id=?";

            CheckConfig record = getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{id},
                    new ExtendedBeanPropertyRowMapper<>(CheckConfig.class)
            );

            setMonitoredEntityForConfigObject(record);

            record.setDbTableName(this.getDbTableName());

            return record;
        }

    @Override
    public List<CheckConfig> getAll() {
        String sql = "SELECT * FROM " + getDbTableName();

        final List<CheckConfig> checkConfigList = getJdbcTemplate().query(
                sql,
                new ExtendedBeanPropertyRowMapper<>(CheckConfig.class));

        setMonitoredEntityForConfigObject(checkConfigList);

        for (CheckConfig checkConfig : checkConfigList) {
            checkConfig.setDbTableName(this.getDbTableName());
        }

        return checkConfigList;
    }

    @Override
    public boolean delete(CheckConfig checkConfig) {
        String sql = "DELETE FROM " + getDbTableName() + " WHERE id=:id";

        getNamedParameterJdbcTemplate().update( sql, getSqlParamsForEntity(checkConfig)[0] );

        return true;
    }

    @Override
    public boolean create(CheckConfig checkConfig) {
        return create( getSqlParamsForEntity( checkConfig ) );
    }
    @Override
    public boolean create(List<CheckConfig> checkConfigList) {
        return create( getSqlParamsForEntity( checkConfigList ) );
    }
    private boolean create(Map<String,Object>[] params){
        String sql = "INSERT INTO " + getDbTableName() + "(entity_id, frequency) " +
                "VALUES ( :entityId, :frequency )";

        getNamedParameterJdbcTemplate().batchUpdate( sql, params );

        return true;
    }

    @Override
    public boolean update(CheckConfig checkConfig) {
        String sql = "UPDATE " + getDbTableName() + " SET " +
                "entity_id = :entityId, frequency = :frequency, " +
                "disabled = :disabled, description = :description " +
                "WHERE id = :id";

        getNamedParameterJdbcTemplate().update( sql, getSqlParamsForEntity(checkConfig)[0] );

        return true;
    }

    private Map<String, Object>[] getSqlParamsForEntity(CheckConfig checkConfig){
        List<CheckConfig> checkConfigList = new ArrayList<>();
        checkConfigList.add(checkConfig);

        return getSqlParamsForEntity( checkConfigList );
    }
    private Map<String, Object>[] getSqlParamsForEntity(List<CheckConfig> checkConfigList){
        Map<String, Object>[] result = (Map<String, Object>[]) new Map[checkConfigList.size()];

        for (int i=0; i<checkConfigList.size(); i++) {
            Map<String, Object> params = new HashMap<>();
            CheckConfig checkConfig = checkConfigList.get(i);
            params.put("id", checkConfig.getId());
            params.put("entityId", checkConfig.getEntityId());
            params.put("frequency", checkConfig.getFrequency());
            params.put("disabled", ""+checkConfig.isDisabled());
            params.put("description", ""+checkConfig.getDescription());
            result[i] = params;
        }

        return result;
    }

    /**
     * Method receive list of checkConfig instances, read .entityId, seek this entity by id in dao
     * and add it to .setEntity(_entity_)
     * @param checkConfigList list of prepared CheckConfig instances
     */
    private void setMonitoredEntityForConfigObject(List<CheckConfig> checkConfigList){
        if (monitoredEntityDao==null || checkConfigList==null || checkConfigList.isEmpty()) return;

        for (CheckConfig checkConfig : checkConfigList) {
            final int entityId = checkConfig.getEntityId();
            final AbstractMonitoredEntity monitoredEntity = monitoredEntityDao.get(entityId);
            checkConfig.setEntity(monitoredEntity);
        }
    }
    private void setMonitoredEntityForConfigObject(CheckConfig checkConfig){
        setMonitoredEntityForConfigObject( new ArrayList<>(Arrays.asList(checkConfig)));
    }
}
