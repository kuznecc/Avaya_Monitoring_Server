package org.bober.avaya_monitoring.web.controller;

import org.bober.avaya_monitoring.model.dao.iAbstractDao;
import org.bober.avaya_monitoring.model.dao.iCheckConfigDao;
import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.entity.CheckConfig;
import org.bober.avaya_monitoring.web.helper.EntityToHtmlTableHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

import static org.bober.avaya_monitoring.model.helper.CollectionHelper.castList;

/**
 * Restful controller for configuration page
 */
@Controller
@RequestMapping("/configuration/")
public class ConfigController {

    /* autowired list of all checkConfig dao */
    @Resource(name = "daoTaskConfigList")
    private List<iCheckConfigDao> checkConfigDaoList;

    /* autowired list of all monitored entity dao */
    @Resource(name = "daoEntityList")
    private List<iMonitoredEntityDao<AbstractMonitoredEntity>> monitoredEntityDaoList;

    /**
     * Return view of configuration page
     *
     * @return content of view 'configurationPage'
     */
    @RequestMapping(value = "/getView",method = RequestMethod.GET)
    public String getView(ModelMap model){
        List<String> daoTableList = new ArrayList<String>();
        for (iAbstractDao dao : checkConfigDaoList) {
            daoTableList.add(dao.getDbTableName());
        }

        model.addAttribute("daoTableList", daoTableList);

        return "configurationPage";
    }


    /**
     * Show table with content of chose dao
     * @return view 'abstractEntityToTable'
     */
    @RequestMapping(value = "/showTable/{tableName}", method = RequestMethod.GET)
    public String showTable(@PathVariable String tableName,
                            ModelMap model) {
        // url example : /showTable/servers

        /* put table name to the model*/
        model.addAttribute("tableName", tableName);

        /* seeking specified dao for received table name */
        iCheckConfigDao specifiedDao = null;
        for (iCheckConfigDao dao : checkConfigDaoList) {
            if (dao.getDbTableName().equals(tableName)){
                specifiedDao = dao;
                break;
            }
        }

        /* read all rows from specified dao */
        List<CheckConfig> checkConfigList = null;

        if (specifiedDao!=null) {
            checkConfigList = specifiedDao.getAll();
        }

        /* put rows list to the model */
        model.addAttribute("checkConfigList", checkConfigList);

        /* put to model list of all monitored entities */
        List<AbstractMonitoredEntity> monitoredEntityList = null;

        if (checkConfigList!=null && !checkConfigList.isEmpty()
                && checkConfigList.get(0).getEntity()!=null) {
            monitoredEntityList = getListOfAllMonitoredEntityForOneEntityInstance(
                    checkConfigList.get(0).getEntity()
            );
        }
        model.addAttribute("monitoredEntityList", monitoredEntityList);

        return "checkConfigEditorView";
    }

    /**
     * Return html-code of CheckConfig view-only table row
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = "/getCheckConfigRow/{tableName}/{checkConfigId}", method = RequestMethod.GET)
    public String getCheckConfigRow(@PathVariable String tableName,
                                    @PathVariable int checkConfigId,
                                    ModelMap model) {
        for (iCheckConfigDao dao : checkConfigDaoList) {
            if(dao.getDbTableName().equals(tableName)){
                CheckConfig c = dao.get(checkConfigId);
                return new EntityToHtmlTableHelper().checkConfigToHtmlTableRow(c);
            }
        }
        return "Can't prepare edit row for (table:"+tableName+", id:"+checkConfigId +")";
    }

    /**
     * Return html-code of CheckConfig editor table row
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = "/getCheckConfigEditableRow/{tableName}/{checkConfigId}", method = RequestMethod.GET)
    public String getCheckConfigEditableRow(@PathVariable String tableName,
                                            @PathVariable int checkConfigId,
                                            ModelMap model) {
        for (iCheckConfigDao dao : checkConfigDaoList) {
            if(dao.getDbTableName().equals(tableName)){
                CheckConfig c = dao.get(checkConfigId);
                return
                        new EntityToHtmlTableHelper().checkConfigToHtmlTableEditableRow(
                                c, getListOfAllMonitoredEntityForOneEntityInstance(c.getEntity())
                        );
            }
        }
        return "Can't prepare edit row for (table:"+tableName+", id:"+checkConfigId +")";
    }

    /**
     * Return html-code of CheckConfig editor table row
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = "/getNewCheckConfigEditableRow/{tableName}", method = RequestMethod.GET)
    public String getNewCheckConfigEditableRow(@PathVariable String tableName, ModelMap model) {
        final CheckConfig config = new CheckConfig();
        config.setId(-1);
        config.setDbTableName(tableName);

        return
                new EntityToHtmlTableHelper().newCheckConfigToHtmlTableEditableRow(
                config, getListOfAllMonitoredEntityForOneEntityInstance(config.getEntity())
        );
    }

    /**
     * This method update entity record by means of dao object and
     * return html-code of loaded from dao CheckConfig table row
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = "/updateCheckConfigEntity/{tableName}/{checkConfigId}", method = RequestMethod.GET)
    public String updateCheckConfigEntity(@PathVariable String tableName,
                                            @PathVariable int checkConfigId,
                                            @RequestParam("entityId") int entityId,
                                            @RequestParam("attributes") String attributes,
                                            @RequestParam("frequency") int frequency,
                                            @RequestParam("disabled") boolean disabled,
                                            @RequestParam("description") String description,
                                            ModelMap model) {
        final CheckConfig config = new CheckConfig();
        config.setId(checkConfigId);
        config.setEntityId(entityId);
        config.setAttributes(attributes);
        config.setFrequency(frequency);
        config.setDisabled(disabled);
        config.setDescription(description);

        return "<tr><td colspan='7'>"+config.toString()+"</td ></tr>";
//
//        /* Seeking certain dao */
//        for (iCheckConfigDao dao : checkConfigDaoList) {
//            if(dao.getDbTableName().equals(tableName)){
//                dao.update(config);
//
//                CheckConfig c = dao.get(checkConfigId);
//
//                return new EntityToHtmlTableHelper().checkConfigToHtmlTableRow(c);
//            }
//        }
//        return "Can't load saved CheckConfig entity (table:"+tableName+", id:"+checkConfigId +")";
    }

    /**
     * This method update entity record by means of dao object and
     * return html-code of loaded from dao CheckConfig table row
     * @return raw html
     */
    @ResponseBody
    @RequestMapping(value = "/createCheckConfigEntity/{tableName}/{checkConfigId}", method = RequestMethod.GET)
    public String createCheckConfigEntity(@PathVariable String tableName,
                                          @PathVariable int checkConfigId,
                                          @RequestParam("entityId") int entityId,
                                          @RequestParam("attributes") String attributes,
                                          @RequestParam("frequency") int frequency,
                                          @RequestParam("disabled") boolean disabled,
                                          @RequestParam("description") String description,
                                          ModelMap model) {
        //createCheckConfigEntity/servers_ping_cfg/-1?entityId=&attributes=&frequency=0&disabled=false&description=
        final CheckConfig config = new CheckConfig();
        config.setId(checkConfigId);
        config.setEntityId(entityId);
        config.setAttributes(attributes);
        config.setFrequency(frequency);
        config.setDisabled(disabled);
        config.setDescription(description);

        return config.toString();
    }

    /* This method return list of all monitored entities from specified dao
        which will be found by class type of received in argument monitored entity instance
    */
    private List<AbstractMonitoredEntity>
    getListOfAllMonitoredEntityForOneEntityInstance
            (AbstractMonitoredEntity entity)
    {
        List<AbstractMonitoredEntity> result = new ArrayList<>();

        if (monitoredEntityDaoList != null && entity != null) {
            for (iMonitoredEntityDao monitoredEntityDao : monitoredEntityDaoList) {

                if (monitoredEntityDao.getMonitoredEntityClass().isInstance(entity)) {
                    result.addAll(
                            castList(
                                    AbstractMonitoredEntity.class,
                                    monitoredEntityDao.getAll()
                            )
                    );
                }
            }
        }

        return result;
    }
}
