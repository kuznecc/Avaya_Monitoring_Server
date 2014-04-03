package org.bober.avaya_monitoring.model.dao.impl;

import org.bober.avaya_monitoring.model.helper.ExtendedBeanPropertyRowMapper;
import org.bober.avaya_monitoring.model.dao.iCheckResultDao;
import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.entity.CheckResult;

import javax.annotation.Resource;
import java.util.*;

import static org.bober.avaya_monitoring.model.helper.DateHelper.dateToSqlFormat;


/**
 * Dao-class for access to all db tables, that consist fields for CheckResult entity.
 */
public class ResultTableDaoJdbc extends AbstractDaoJdbc implements iCheckResultDao {

    @Override
    public CheckResult get(int id) {
        String sql = "SELECT * FROM " + getDbTableName() + " WHERE id=? ORDER BY date";

        CheckResult result = getJdbcTemplate().queryForObject(
                sql,
                new Object[]{id},
                new ExtendedBeanPropertyRowMapper<>(CheckResult.class)
        );

        applyServerProperty( result );

        return result;
    }

    @Override
    public List<CheckResult> get(Date startDate, Date endDate){
        String sql = "SELECT * FROM " + getDbTableName() + " WHERE date BETWEEN ? AND ? ORDER BY date";

        List<CheckResult> result = getJdbcTemplate().query(
                sql,
                new Object[]{dateToSqlFormat(startDate), dateToSqlFormat(endDate)},
                new ExtendedBeanPropertyRowMapper<>(CheckResult.class)
        );

        applyServerProperty( result );

        return result;
    }


    @Override
    public List<CheckResult> getAll() {
        String sql = "SELECT * FROM " + getDbTableName() + " ORDER BY date";

        List<CheckResult> result = getJdbcTemplate().query(
                sql,
                new ExtendedBeanPropertyRowMapper<>(CheckResult.class));

        applyServerProperty( result );

        return result;
    }

    /**
     * With this dao will be load monitored entity objects by entityId, and
     * added to CheckResult instances
     */
    @Resource(name = "serverDao")
    private iMonitoredEntityDao<AbstractMonitoredEntity> resultEntityDao;
    public void setResultEntityDao(iMonitoredEntityDao<AbstractMonitoredEntity> resultEntityDao) {
        this.resultEntityDao = resultEntityDao;
    }

    /* add server property to received CheckResult List by serverId property value */
    private void applyServerProperty(List<CheckResult> pingResultList){
        Map<Integer, AbstractMonitoredEntity> serversMap = resultEntityDao.getEntityMap();

        for (CheckResult checkResult : pingResultList) {
            AbstractMonitoredEntity entity = serversMap.get(checkResult.getEntityId());
            checkResult.setEntity(entity);
        }
    }
    private void  applyServerProperty(CheckResult pingResult){
        pingResult.setEntity(
                resultEntityDao.get(pingResult.getEntityId())
        );
    }

    @Override
    public boolean delete(CheckResult checkResult) {
        String sql = "DELETE FROM " + getDbTableName() + " WHERE id=:id";

        getNamedParameterJdbcTemplate().update( sql, getSqlParamsForEntity(checkResult)[0] );

        return true;
    }

    @Override
    public boolean create(CheckResult checkResult) {
        return create( getSqlParamsForEntity( checkResult ) );
    }
    @Override
    public boolean create(List<CheckResult> checkResultList) {
        return create( getSqlParamsForEntity( checkResultList ) );
    }
    private boolean create(Map<String,Object>[] params) {
        String sql = "INSERT INTO " + getDbTableName() + "(date, entity_id, attributes, value) " +
                "VALUES ( :date, :entityId, :attributes, :value)";

        getNamedParameterJdbcTemplate().batchUpdate(sql, params );

        return true;
    }

    @Override
    public boolean update(CheckResult checkResult) {

        String sql = "UPDATE " + getDbTableName() + " SET " +
                "date = :date, entity_id = :entityId, value = :value, attributes = :attributes" +
                " WHERE id = :id";

        getNamedParameterJdbcTemplate().update( sql, getSqlParamsForEntity(checkResult)[0] );

        return true;
    }

    private Map<String, Object>[] getSqlParamsForEntity(CheckResult checkResult){
        List<CheckResult> checkConfigList = new ArrayList<>();
        checkConfigList.add(checkResult);

        return getSqlParamsForEntity( checkConfigList );
    }
    private Map<String, Object>[] getSqlParamsForEntity(List<CheckResult> checkResultList){
        Map<String, Object>[] result = (Map<String, Object>[]) new Map[checkResultList.size()];

        for (int i=0; i<checkResultList.size(); i++) {
            Map<String, Object> params = new HashMap<>();
            CheckResult checkResult = checkResultList.get(i);
            params.put("id", checkResult.getId());
            params.put("date", checkResult.getDate() );
            params.put("entityId", checkResult.getEntityId());
            params.put("attributes", checkResult.getAttributes());
            params.put("value", checkResult.getValue());
            result[i] = params;
        }

        return result;
    }



}
