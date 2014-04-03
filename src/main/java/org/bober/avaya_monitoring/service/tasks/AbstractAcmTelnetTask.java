package org.bober.avaya_monitoring.service.tasks;


import org.bober.avaya_monitoring.model.dao.impl.EntityAvayaParameterDaoJdbc;
import org.bober.avaya_monitoring.model.entity.AvayaParameter;
import org.bober.avaya_monitoring.model.entity.CheckConfig;
import org.bober.avaya_monitoring.model.entity.Server;
import org.bober.avaya_monitoring.service.tasks.util.AcmTelnetConnection;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * Common class for all tasks which make acm-telnet checks
 */
public abstract class AbstractAcmTelnetTask extends AbstractTask{

    /* inject EntityAvayaParameterDaoJdbc bean for access to avaya_parameter table */
    @Resource(name = "avayaParameterDao")
    private EntityAvayaParameterDaoJdbc avayaParameterDao;
    protected EntityAvayaParameterDaoJdbc getAvayaParameterDao() {
        return avayaParameterDao;
    }

    /**
     * This field consist AcmTelnetConnection bean that will be user for telnet requests to ACM-server.
     * Bean "acmTelnetConnection" was prototype.
     * If you need to use same acmTelnetConnection bean instance in the several tasks - you must inject it
     * to this property in the specified task bean of spring context file.
     */
    @Resource(name = "acmTelnetConnection")
    private AcmTelnetConnection acmTelnetConnection;
    public AcmTelnetConnection getAcmTelnetConnection() {
        return acmTelnetConnection;
    }
    public void setAcmTelnetConnection(AcmTelnetConnection acmTelnetConnection) {
        this.acmTelnetConnection = acmTelnetConnection;
    }


    /**
     * Additional field of all telnet tasks which contains entity of telnet-server.
     * This Server entity will be used for making new telnet connection
     */
    Server monitoredEntityServer;
    public Server getMonitoredEntityServer() {
        return monitoredEntityServer;
    }
    public void setMonitoredEntityServer(Server monitoredEntityServer) {
        this.monitoredEntityServer = monitoredEntityServer;
    }

    /**
     * This implementation of setCheckConfig for the else set field monitoredEntityServer
     *
     * @param checkConfig - configuration of this task
     */
    @Override
    public void setCheckCfg(CheckConfig checkConfig) {
        this.checkConfig = checkConfig;
        AvayaParameter avayaParameter = getAvayaParameterDao().get(checkConfig.getEntityId());
        setMonitoredEntity(avayaParameter);
        setMonitoredEntityServer( getServerDao().get( avayaParameter.getServerId() ) );
        setPeriod(checkConfig.getFrequency());
        setDisabled(checkConfig.isDisabled());
    }


    /**
     * Method receive template date in millis like "00:00 14.02.2014" and list of hours from report rows.
     * Method iterate hours list, understand which mean yesterday and which mean today, translate it to millis
     * and add result to template date for each row.
     *
     * This method needed for tasks which parse commands like : "list bcms vdn 98200", "list bcms trunk 2"
     *
     * @return list of row dates in millis
     */
    protected static List<Long> getDateList(List<Integer>time, long resultDate){
        List<Long> result = new ArrayList<>();

        final int oneDayInMillis = 1000 * 60 * 60 * 24;
        final int oneHourInMillis = 1000 * 60 * 60;

        resultDate -= oneDayInMillis;

        for (Integer aTime : time) {
            if (aTime == 0) {
                resultDate += oneDayInMillis;
            }
            result.add(resultDate + (oneHourInMillis * aTime));
        }

        return result;
    }


    @Override
    public String getDescription(){
        return String.format(
                "Это абстрактный таск для всех заданий, которые будут парсить результаты выполнения" +
                        "команд в телнет-сессии ACM."
        );
    }

}
