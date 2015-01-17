package org.bober.avaya_monitoring.web.controller;

import org.bober.avaya_monitoring.model.dao.iAbstractDao;
import org.bober.avaya_monitoring.model.dao.iCheckConfigDao;
import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.*;
import org.bober.avaya_monitoring.model.helper.CollectionHelper;
import org.bober.avaya_monitoring.web.helper.ConfigurationPageTableHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.bober.avaya_monitoring.web.helper.UrlHelper.*;

/**
 * This controller servicing requests from ConfigurationPage of Avaya Monitoring Project
 */

@Controller
@RequestMapping(URL_CONFIG_PAGE_ROOT)
public class ConfigurationController {

    /* autowired list */
    @Resource(name = "allConfigurableEntitiesDaoList")
    private List<iAbstractDao<AbstractEntity>> allConfigurableEntitiesDaoList;

    @Resource(name = "serverDao")
    private iAbstractDao<Server> serverDao;


    /**
     * Return view of configuration page
     *
     * @return content of view 'configurationPage'
     */
    @RequestMapping(value = URL_CONFIG_PAGE_GET_VIEW, method = RequestMethod.GET)
    public String getView(ModelMap model){
        List<String> daoTableList = new ArrayList<>();

        for (iAbstractDao dao : allConfigurableEntitiesDaoList) {
            daoTableList.add(dao.getDbTableName());
        }

        model.addAttribute("daoTableList", daoTableList);

        return "configurationPage";
    }

    /**
     * Return code of table with all content of chosen dao
     *
     * @return view 'abstractEntityToTable'
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_GET_TABLE, method = RequestMethod.GET)
    public String getTable(@PathVariable(URL_VAR_TABLE_NAME) String tableName) {
        List<AbstractEntity> entityList = getAllAbstractEntitiesFromDao(tableName);

        return ConfigurationPageTableHelper.getTable(tableName, entityList);
    }

    /**
     * Method do:
     *      - obtain name of target DbTable and seek dao for it
     *      - load all records from table and return it like a list
     * If method can't load records for obtained tableName then return null
     *
     * @param tableName name of DbTable
     * @return list of all records from needed table
     */
    private List<AbstractEntity> getAllAbstractEntitiesFromDao(String tableName) {
        /* seeking specified dao for received tableName */
        iAbstractDao<AbstractEntity> specifiedDao = getDaoForTableName(tableName);

        /* getting all records from specified dao and add it to model */
        List<AbstractEntity> entityList = null;

        if (specifiedDao != null) {
            entityList = specifiedDao.getAll();
        }

        return entityList;
    }

    private iAbstractDao<AbstractEntity> getDaoForTableName(String tableName) {
        iAbstractDao<AbstractEntity> specifiedDao = null;
        for (iAbstractDao<AbstractEntity> dao : allConfigurableEntitiesDaoList) {
            if (dao.getDbTableName().equals(tableName)){
                specifiedDao = dao;
                break;
            }
        }
        return specifiedDao;
    }

    private iCheckConfigDao getCheckConfigDaoForTableName(String tableName) {
        iCheckConfigDao specifiedDao = null;
        for (Object obj : allConfigurableEntitiesDaoList) {
            if (obj instanceof iCheckConfigDao){
                iCheckConfigDao dao = (iCheckConfigDao) obj;
                if (dao.getDbTableName().equals(tableName)){
                    specifiedDao = dao;
                    break;
                }
            }
        }
        return specifiedDao;
    }

    private Object getDaoObjectForTableName(String tableName) {
        Object specifiedDao = null;
        for (Object obj : allConfigurableEntitiesDaoList) {
            if (obj instanceof iAbstractDao){
                iAbstractDao abstractDao = (iAbstractDao) obj;
                if (abstractDao.getDbTableName().equals(tableName)){
                    specifiedDao = obj;
                    break;
                }
            }
        }
        return specifiedDao;
    }


    /**
     * Return html-code of view-only table row
     *
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_GET_VIEW_ROW, method = RequestMethod.GET)
    public String getViewRow(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                             @PathVariable(URL_VAR_ENTITY_ID) int entityId) {
        for (iAbstractDao<AbstractEntity> dao : allConfigurableEntitiesDaoList) {
            if(dao.getDbTableName().equals(tableName)){
                AbstractEntity entity = dao.get(entityId);
                return ConfigurationPageTableHelper.getViewRow(entity);
            }
        }

        return "Can't prepare view row for (table:"+tableName+", id:"+ entityId +")";
    }

    /**
     * Return html-code of Entity editor table row
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_GET_EDIT_ROW, method = RequestMethod.GET)
    public String getEditRow(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                             @PathVariable(URL_VAR_ENTITY_ID) int entityId) {
        AbstractEntity entity=null;

        /* Loading entity object from dao */
        for (iAbstractDao<AbstractEntity> dao : allConfigurableEntitiesDaoList) {
            if(dao.getDbTableName().equals(tableName)){
                entity = dao.get(entityId);
            }
        }

        if (entity==null){
            return "Can't prepare edit row for (table:"+tableName+", id:"+ entityId +")";
        }

        /* If entity is an instance of CheckConfig */
        if (entity instanceof CheckConfig) {
            final CheckConfig checkConfig = (CheckConfig) entity;

            if (checkConfig.getEntity() != null) {
                final String monitoredEntityDbTableName = checkConfig.getEntity().getDbTableName();
                List<AbstractMonitoredEntity> monitoredEntityList =
                        CollectionHelper.castList( AbstractMonitoredEntity.class,
                                getAllAbstractEntitiesFromDao(monitoredEntityDbTableName) );

                return ConfigurationPageTableHelper.getEditRow(entity, monitoredEntityList);
            }
        }

        /* If entity is an instance of AvayaParameter */
        if (entity instanceof AvayaParameter) {
            final AvayaParameter avayaParameter = (AvayaParameter) entity;

            if (avayaParameter.getServer() != null) {
                final String monitoredEntityDbTableName = avayaParameter.getServer().getDbTableName();
                List<AbstractMonitoredEntity> monitoredEntityList =
                        CollectionHelper.castList(AbstractMonitoredEntity.class,
                                getAllAbstractEntitiesFromDao(monitoredEntityDbTableName) );

                return ConfigurationPageTableHelper.getEditRow(entity, monitoredEntityList);
            }
        }

        return ConfigurationPageTableHelper.getEditRow(entity);
    }


    /**
     * This method update CheckConfig record in the DB and return viewRow with reloaded from DB entity
     *
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_UPDATE_CHECK_CONFIG, method = RequestMethod.GET)
    public String updateEntityCheckConfig(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                                          @PathVariable(URL_VAR_ENTITY_ID) int entityId,
                                          @RequestParam(URL_PROPERTY_MON_ENTITY_ID) int monitoredEntityId,
                                          @RequestParam(URL_PROPERTY_ATTRIBUTES) String attributes,
                                          @RequestParam(URL_PROPERTY_FREQUENCY) int frequency,
                                          @RequestParam(URL_PROPERTY_DISABLED) boolean disabled,
                                          @RequestParam(URL_PROPERTY_DESCRIPTION) String description) {
        // http://localhost:8080/configuration/updateEntityCheckConfig/servers_ping_cfg/1?monEntityId=1&attributes=&frequency=300000&disabled=false&description=
        final CheckConfig config = new CheckConfig();
        config.setDbTableName(tableName);
        config.setId(entityId);
        config.setEntityId(monitoredEntityId);
        config.setAttributes(attributes);
        config.setFrequency(frequency);
        config.setDisabled(disabled);
        config.setDescription(description);

        return getViewRow(tableName, entityId);
    }

    /**
     * This method update Server record in the DB and return viewRow with reloaded from DB entity
     *
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_UPDATE_SERVER, method = RequestMethod.GET)
    public String updateEntityServer(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                                          @PathVariable(URL_VAR_ENTITY_ID) int id,
                                          @RequestParam(URL_PROPERTY_NAME) String name,
                                          @RequestParam(URL_PROPERTY_DELETED) boolean deleted,
                                          @RequestParam(URL_PROPERTY_SERVER_IP) String ip,
                                          @RequestParam(URL_PROPERTY_SERVER_SNMP_COMMUNITY) String snmpCommunity,
                                          @RequestParam(URL_PROPERTY_SERVER_OS_TYPE) String osType,
                                          @RequestParam(URL_PROPERTY_DESCRIPTION) String description) {
        final Server server = new Server();
        server.setDbTableName(tableName);
        server.setId(id);
        server.setName(name);
        server.setDeleted(deleted);
        server.setIp(ip);
        server.setSnmpCommunity(snmpCommunity);
        server.setOsType(osType);
        server.setDescription(description);

        return getViewRow(tableName, id);
    }

    /**
     * This method update AvayaParameter record in the DB and return viewRow with reloaded from DB entity
     *
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_UPDATE_AVAYA_PARAMETER, method = RequestMethod.GET)
    public String updateEntityAvayaParameter(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                                          @PathVariable(URL_VAR_ENTITY_ID) int id,
                                          @RequestParam(URL_PROPERTY_NAME) String name,
                                          @RequestParam(URL_PROPERTY_DELETED) boolean deleted,
                                          @RequestParam(URL_PROPERTY_SERVER_ID) int serverId,
                                          @RequestParam(URL_PROPERTY_SUBSYSTEM) String subsystem,
                                          @RequestParam(URL_PROPERTY_DESCRIPTION) String description) {
        final AvayaParameter avayaParameter = new AvayaParameter();
        avayaParameter.setDbTableName(tableName);
        avayaParameter.setId(id);
        avayaParameter.setName(name);
        avayaParameter.setDeleted(deleted);
        avayaParameter.setServerId(serverId);
        avayaParameter.setSubSystem(subsystem);
        avayaParameter.setDescription(description);

        return getViewRow(tableName, id);
    }

    /**
     * EDITABLE ROW WITH NEW ENTITY
     * Return html-code of AvayaParameter editor table row
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_ADD_NEW_ROW, method = RequestMethod.GET)
    public String getAddNewRow(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                               @PathVariable(URL_VAR_ENTITY_TYPE) String entityType) {
        AbstractEntity entity;
        List<AbstractMonitoredEntity> monitoredEntityList= null;

        /* entityType == Server */
        if (entityType.equals(Server.class.getSimpleName())){
            final Server server = new Server();
            server.setId(-1);
            server.setDbTableName(tableName);
            server.setIp("0.0.0.0");

            entity = server;
        } else
        /* entityType == AvayaParameter */
        if (entityType.equals(AvayaParameter.class.getSimpleName())){
            final AvayaParameter avayaParameter = new AvayaParameter();
            avayaParameter.setId(-1);
            avayaParameter.setDbTableName(tableName);

            entity = avayaParameter;
            monitoredEntityList = CollectionHelper.castList(
                    AbstractMonitoredEntity.class,
                    getAllAbstractEntitiesFromDao(serverDao.getDbTableName())
            );
        } else
        /* entityType == CheckConfig */
        if (entityType.equals(CheckConfig.class.getSimpleName())){
            final CheckConfig checkConfig = new CheckConfig();
            checkConfig.setId(-1);
            checkConfig.setDbTableName(tableName);

            entity = checkConfig;

            final String monitoredEntityDbTableName =
                    getCheckConfigDaoForTableName(tableName).getMonitoredEntityDao().getDbTableName();
            monitoredEntityList = CollectionHelper.castList( AbstractMonitoredEntity.class,
                    getAllAbstractEntitiesFromDao( monitoredEntityDbTableName) );
        } else
        /* entityType == Unknown type */
        {
            return "Can't prepare new entity edit row for (table:"+tableName+", entityType:"+ entityType +")";
        }

        return  ConfigurationPageTableHelper.getAddNewEntityEditRow(entity, monitoredEntityList);

    }

    /**
     * This method CREATE NEW ENTITY SERVER
     *
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_CREATE_SERVER, method = RequestMethod.GET)
    public String createEntityServer(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                                     @RequestParam(URL_PROPERTY_NAME) String name,
                                     @RequestParam(URL_PROPERTY_DELETED) boolean deleted,
                                     @RequestParam(URL_PROPERTY_SERVER_IP) String ip,
                                     @RequestParam(URL_PROPERTY_SERVER_SNMP_COMMUNITY) String snmpCommunity,
                                     @RequestParam(URL_PROPERTY_SERVER_OS_TYPE) String osType,
                                     @RequestParam(URL_PROPERTY_DESCRIPTION) String description) {
        /* create new server instance */
        final Server server = new Server();
        server.setDbTableName(tableName);
        server.setName(name);
        server.setDeleted(deleted);
        server.setIp(ip);
        server.setSnmpCommunity(snmpCommunity);
        server.setOsType(osType);
        server.setDescription(description);

        /* seeking for a specified dao */
        iMonitoredEntityDao<Server> serverDao = null;
        final Object daoByTableName = getDaoObjectForTableName(tableName);

        if (daoByTableName != null &&
                daoByTableName instanceof iMonitoredEntityDao){
                serverDao = (iMonitoredEntityDao<Server>) daoByTableName;
        }

        if (serverDao == null){
            return "ERROR: Can't find dao for table '"+tableName+"'\n" + server.toString();
        }

        serverDao.create(server);

        return "New server entity is uploaded to BD\n" + server.toString();
    }

    /**
     * This method CREATE NEW AVAYA PARAMETER
     *
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_CREATE_AVAYA_PARAMETER, method = RequestMethod.GET)
    public String createEntityAvayaParameter(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                                             @RequestParam(URL_PROPERTY_NAME) String name,
                                             @RequestParam(URL_PROPERTY_DELETED) boolean deleted,
                                             @RequestParam(URL_PROPERTY_SERVER_ID) int serverId,
                                             @RequestParam(URL_PROPERTY_SUBSYSTEM) String subsystem,
                                             @RequestParam(URL_PROPERTY_DESCRIPTION) String description) {
            // name=kv-vpms-02&deleted=false&srvIp=10.7.1.62&srvSnmpCommunity=PUBLIC_COMMON&srvOsType=linux&description=
            final AvayaParameter avayaParameter = new AvayaParameter();
            avayaParameter.setDbTableName(tableName);
            avayaParameter.setName(name);
            avayaParameter.setDeleted(deleted);
            avayaParameter.setServerId(serverId);
            avayaParameter.setSubSystem(subsystem);
            avayaParameter.setDescription(description);

        return "in future new entity will be uploaded to BD\n" + avayaParameter.toString();
    }

    /**
     * This method CREATE NEW CHECK CONFIG
     *
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = URL_CONFIG_PAGE_CREATE_CHECK_CONFIG, method = RequestMethod.GET)
    public String createEntityCheckConfig(@PathVariable(URL_VAR_TABLE_NAME) String tableName,
                                          @RequestParam(URL_PROPERTY_MON_ENTITY_ID) int monitoredEntityId,
                                          @RequestParam(URL_PROPERTY_ATTRIBUTES) String attributes,
                                          @RequestParam(URL_PROPERTY_FREQUENCY) int frequency,
                                          @RequestParam(URL_PROPERTY_DISABLED) boolean disabled,
                                          @RequestParam(URL_PROPERTY_DESCRIPTION) String description) {
        final CheckConfig config = new CheckConfig();
        config.setDbTableName(tableName);
        config.setEntityId(monitoredEntityId);
        config.setAttributes(attributes);
        config.setFrequency(frequency);
        config.setDisabled(disabled);
        config.setDescription(description);

        return "in future new entity will be uploaded to BD\n" + config.toString();
    }

}
