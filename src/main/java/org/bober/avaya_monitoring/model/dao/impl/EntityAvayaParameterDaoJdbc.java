package org.bober.avaya_monitoring.model.dao.impl;


import org.bober.avaya_monitoring.model.helper.ExtendedBeanPropertyRowMapper;
import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.AvayaParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dao-class for access to 'avaya_parameters' db tables, that consist fields for AvayaParameter entity.
 */
public class EntityAvayaParameterDaoJdbc extends AbstractDaoJdbc
        implements iMonitoredEntityDao<AvayaParameter> {

    {
        setDbTableName("avaya_parameters");
    }

    @Override
    public Class<AvayaParameter> getMonitoredEntityClass() {
        return AvayaParameter.class;
    }

    @Override
    public AvayaParameter get(int id) {
        String sql = "SELECT * FROM " + getDbTableName() + " WHERE id=?";

        AvayaParameter record = getJdbcTemplate().queryForObject(
                sql,
                new Object[]{id},
                new ExtendedBeanPropertyRowMapper<>(AvayaParameter.class)
        );

        record.setDbTableName(this.getDbTableName());

        return record;
    }

    @Override
    public AvayaParameter get(String name){
        String sql = "SELECT * FROM " + getDbTableName() + " WHERE name=?";

        AvayaParameter record = getJdbcTemplate().queryForObject(
                sql,
                new Object[]{name},
                new ExtendedBeanPropertyRowMapper<>(AvayaParameter.class)
        );

        record.setDbTableName(this.getDbTableName());

        return record;
    }

    @Override
    public List<AvayaParameter> getAll() {
        String sql = "SELECT * FROM " + getDbTableName();

        List<AvayaParameter> parameters = getJdbcTemplate().query(
                sql,
                new ExtendedBeanPropertyRowMapper<>(AvayaParameter.class));

        for (AvayaParameter avayaParameter : parameters) {
            avayaParameter.setDbTableName(this.getDbTableName());
        }

        return parameters;
    }

    /* Get map of all AvayaParameters {srvId, parameter} */
    @Override
    public Map<Integer, AvayaParameter> getEntityMap() {
        List<AvayaParameter> parameters = getAll();
        Map<Integer, AvayaParameter> result = new HashMap<>();

        for (AvayaParameter parameter : parameters) {
            result.put(parameter.getId(), parameter);
        }

        return result;
    }

    @Override
    public boolean delete(AvayaParameter parameter) {
        String sql = "DELETE FROM " + getDbTableName() + " WHERE id=:id";

        getNamedParameterJdbcTemplate().update( sql, getSqlParamsForEntity(parameter)[0] );

        return true;
    }


    @Override
    public boolean create(AvayaParameter parameter) {
        return create(getSqlParamsForEntity(parameter));
    }
    @Override
    public boolean create(List<AvayaParameter> parameterList) {
        return create(getSqlParamsForEntity( parameterList ));
    }
    private boolean create(Map<String,Object>[] params) {
        String sql = "INSERT INTO " + getDbTableName() +
                "(name, subsystem, server_id, deleted, description) " +
                "VALUES ( :name, :subsystem, :serverId, :deleted, :description )";
        /* problem with 'cast' bool to MySQL Enum. Got SQL exception. */
        //SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(serversList.toArray());
        getNamedParameterJdbcTemplate().batchUpdate(sql, params );

        return true;
    }

    @Override
    public boolean update(AvayaParameter parameter) {
        String sql = "UPDATE " + getDbTableName() + " SET " +
                "name = :name, subsystem = :subsystem, server_id = :serverId, " +
                "deleted = :deleted, description = :description " +
                "WHERE id = :id";

        getNamedParameterJdbcTemplate().update(sql, getSqlParamsForEntity(parameter)[0]);

        return true;
    }

    private Map<String, Object>[] getSqlParamsForEntity(AvayaParameter parameter){
        List<AvayaParameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);

        return getSqlParamsForEntity( parameterList );
    }
    private Map<String, Object>[] getSqlParamsForEntity(List<AvayaParameter> parameterList){
        Map<String, Object>[] result = (Map<String, Object>[]) new Map[parameterList.size()];

        for (int i=0; i<parameterList.size(); i++) {
            Map<String, Object> params = new HashMap<>();
            AvayaParameter parameter = parameterList.get(i);
            params.put("id", parameter.getId());
            params.put("name", parameter.getName());
            params.put("subsystem", parameter.getSubSystem());
            params.put("serverId", parameter.getServerId());
            params.put("deleted", ""+parameter.isDeleted());
            params.put("description", parameter.getDescription());

            result[i] = params;
        }

        return result;
    }





}
