package org.bober.avaya_monitoring.web.controller;

import org.bober.avaya_monitoring.model.dao.iAbstractDao;
import org.bober.avaya_monitoring.model.dao.iCheckConfigDao;
import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.AbstractEntity;
import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.entity.CheckConfig;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.helper.CollectionHelper;
import org.bober.avaya_monitoring.model.helper.DateHelper;
import org.bober.avaya_monitoring.web.helper.EntityToHtmlTableHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
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
    public String returnDao(@PathVariable String tableName,
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

        return "checkResultEditorView";
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
