package org.bober.avaya_monitoring.web.controller;

import org.bober.avaya_monitoring.model.dao.iAbstractDao;
import org.bober.avaya_monitoring.model.dao.iCheckResultDao;
import org.bober.avaya_monitoring.model.entity.AbstractEntity;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.helper.CollectionHelper;
import org.bober.avaya_monitoring.model.helper.DateHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Restful controller for statistics page
 */

@Controller
@RequestMapping("/statistics/")
public class StatisticsController {

    /* autowired list of all tables dao */
    @Resource(name = "daoList")
    private List<iAbstractDao> daoList;


    /**
     * Return view of statistics view page
     *
     * @return content of view 'statisticsPage'
     */
    @RequestMapping(value = "/getView",method = RequestMethod.GET)
    public String getView(ModelMap model){
        List<String> daoTableList = new ArrayList<String>();
        for (iAbstractDao dao : daoList) {
            daoTableList.add(dao.getDbTableName());
        }

        model.addAttribute("daoTableList", daoTableList);

        return "statisticsPage";
    }

    /**
     * Return data for N days. Doesn't work.
     */
    @Deprecated
    @RequestMapping(value = "/getForDays/{tableName}", method = RequestMethod.GET)
    public String returnDao(@PathVariable String tableName,
                            @RequestParam("days") int days,
                            @RequestParam("type") String type,
                            ModelMap model) {
        model.addAttribute("comment",
                "received params : days=" + days + ", type=" + type + "<br>");

        addDataForPeriodToModel( tableName, days, model);

        return "abstractEntityToTable";
    }

    /**
     * Return data for specified period.
     * @return if table consist CheckResult objects,
     *      then return view 'checkResultToLineChart'
     *      else - return view 'abstractEntityToTable'
     */
    @RequestMapping(value = "/getForPeriod/{tableName}", method = RequestMethod.GET)
    public String returnDao(@PathVariable String tableName,
                            @RequestParam("startDate") String startDate,
                            @RequestParam("endDate") String endDate,
                            ModelMap model) {
        // url example : /dao/servers?startDate=2014-02-13_00.00.00&endDate=2014-02-13_23.59.00
        model.addAttribute("comment", "received params : startDate=" + startDate +
                ", endDate=" + endDate + "<br>");

        model.addAttribute("tableName", tableName);

        Date sDate = null,eDate = null;
        try {
            sDate = DateHelper.dateParseFromMyJsFormat(startDate);
            eDate = DateHelper.dateParseFromMyJsFormat(endDate);
        } catch (ParseException e) {
            System.err.println( e );
            e.printStackTrace();
        }

        List<AbstractEntity> entityList = null;

        if (sDate!=null && eDate!= null){
            entityList = addDataForPeriodToModel( tableName, sDate, eDate, model);
        }

        // all server check results show like a graph
        if (entityList != null && entityList.size() >0 &&
                entityList.get(0) instanceof CheckResult) {
            return "checkResultToLineChart";
        }

        return "abstractEntityToTable";
    }





    /**
     * Adding entity list to the received model object
     */
    private List<AbstractEntity> getDataForPeriod(String tableName, Date sDate, Date eDate, ModelMap model){
        List<AbstractEntity> result = new ArrayList<>();

        for (iAbstractDao dao : daoList) {
            if ( tableName.equals(dao.getDbTableName()) ){
                if (dao instanceof iCheckResultDao) {
                    return CollectionHelper.castList(AbstractEntity.class, ((iCheckResultDao) dao).get(sDate, eDate)) ;
                } else {
                    return CollectionHelper.castList(AbstractEntity.class, dao.getAll()) ;
                }
            }
        }

        throw new IllegalArgumentException("Can not find dao for table \'"+tableName+"\'");
    }

    private List<AbstractEntity> addDataForPeriodToModel(String tableName, Date sDate, Date eDate, ModelMap model){
        for (iAbstractDao dao : daoList) {
            List<AbstractEntity> entityList = null;

            if ( tableName.equals(dao.getDbTableName()) ){
                if (dao instanceof iCheckResultDao) {
                    entityList = CollectionHelper.castList(AbstractEntity.class, ((iCheckResultDao) dao).get(sDate, eDate));
                } else {
                    entityList = CollectionHelper.castList(AbstractEntity.class, dao.getAll());
                }

                model.addAttribute("entityList", entityList);

                return entityList;
            }
        }

        return null;
    }
    private void addDataForPeriodToModel(String tableName, int days, ModelMap model){
        Date now = new Date();
        Date sDate = new Date(now.getTime() - (days*1000*24*60*60) );

        addDataForPeriodToModel( tableName, sDate, now, model );
    }
}