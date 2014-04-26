package org.bober.avaya_monitoring.model.dao.impl;


import org.bober.avaya_monitoring.model.helper.ExtendedBeanPropertyRowMapper;
import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dao-class for access to 'servers' db table, that consist fields for Server entity.
 */
public class EntityServerDaoJdbc extends AbstractDaoJdbc
        implements iMonitoredEntityDao<Server> {

    {
        setDbTableName("servers");
    }

    @Override
    public Class<Server> getMonitoredEntityClass() {
        return Server.class;
    }

    @Override
    public Server get(int id) {
        String sql = "SELECT * FROM servers WHERE id=?";

        Server record = getJdbcTemplate().queryForObject(
                sql,
                new Object[]{id},
                new ExtendedBeanPropertyRowMapper<>(Server.class)
        );

        return record;
    }

    @Override
    public Server get(String name){
        String sql = "SELECT * FROM servers WHERE name=?";

        Server record = getJdbcTemplate().queryForObject(
                sql,
                new Object[]{name},
                new ExtendedBeanPropertyRowMapper<>(Server.class)
        );

        return record;
    }

    @Override
    public List<Server> getAll() {
        String sql = "SELECT * FROM servers";

        List<Server> servers = getJdbcTemplate().query(
                sql,
                new ExtendedBeanPropertyRowMapper<>(Server.class));
        return servers;
    }

    /* Get map of all servers {srvId, AbstrMonitoredEntity} */
    @Override
    public Map<Integer, Server> getEntityMap() {
        List<Server> servers = getAll();
        Map<Integer, Server> result = new HashMap<>();

        for (Server server : servers) {
            result.put(server.getId(), server);
        }

        return result;
    }

    @Override
    public boolean delete(Server server) {
        String sql = "DELETE FROM servers WHERE id=:id";

        getNamedParameterJdbcTemplate().update( sql, getSqlParamsForEntity(server)[0] );

        return true;
    }


    @Override
    public boolean create(Server server) {
        return create(getSqlParamsForEntity(server));
    }
    @Override
    public boolean create(List<Server> serversList) {
        return create(getSqlParamsForEntity( serversList ));
    }
    private boolean create(Map<String,Object>[] params) {
        String sql = "INSERT INTO servers(name, ip, os_type, deleted, description, snmp_community) " +
                "VALUES ( :name, :ip, :osType, :deleted, :description, :snmpCommunity )";
        /* problem with 'cast' bool to MySQL Enum. Got SQL exception. */
        //SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(serversList.toArray());
        getNamedParameterJdbcTemplate().batchUpdate(sql, params );

        return true;
    }

    @Override
    public boolean update(Server server) {
        String sql = "UPDATE servers SET " +
                "name = :name, ip = :ip, os_type = :osType, deleted = :deleted, " +
                "description = :description, snmp_community = :snmpCommunity " +
                "WHERE id = :id";

        getNamedParameterJdbcTemplate().update(sql, getSqlParamsForEntity(server)[0]);

        return true;
    }

    private Map<String, Object>[] getSqlParamsForEntity(Server server){
        List<Server> serverList = new ArrayList<>();
        serverList.add( server );

        return getSqlParamsForEntity( serverList );
    }
    private Map<String, Object>[] getSqlParamsForEntity(List<Server> serverList){
        Map<String, Object>[] result = (Map<String, Object>[]) new Map[serverList.size()];

        for (int i=0; i<serverList.size(); i++) {
            Map<String, Object> params = new HashMap<>();
            Server server = serverList.get(i);
            params.put("id", server.getId());
            params.put("name", server.getName());
            params.put("ip", server.getIp());
            params.put("osType", server.getOsType());
            params.put("deleted", ""+server.isDeleted());
            params.put("description", server.getDescription());
            params.put("snmpCommunity", server.getSnmpCommunity());
            result[i] = params;
        }

        return result;
    }




}
