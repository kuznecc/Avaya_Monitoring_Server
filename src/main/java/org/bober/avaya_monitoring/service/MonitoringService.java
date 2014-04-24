package org.bober.avaya_monitoring.service;

import org.bober.avaya_monitoring.model.dao.iCheckConfigDao;
import org.bober.avaya_monitoring.model.entity.CheckConfig;
import org.bober.avaya_monitoring.service.tasks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class of Ping Service which used for handling all monitoring tasks
 */
public class MonitoringService {

    Logger logger = LoggerFactory.getLogger("avayaMon");

    /* autowired dao-beans */
    @Resource(name = "serverPingConfigDao")
    private iCheckConfigDao serverPingConfigDao;

    @Resource(name = "serverCpuLoadCheckConfigDao")
    private iCheckConfigDao serverCpuLoadCheckConfigDao;

    @Resource(name = "serverMemLoadCheckConfigDao")
    private iCheckConfigDao serverMemLoadCheckConfigDao;

    @Resource(name = "snmpVpmsChannelsUsageCheckCfgDao")
    private iCheckConfigDao snmpVpmsChannelsUsageCheckCfgDao;

    @Resource(name = "avayaParametersTelnetBcmsVdnCheckCfgDao")
    private iCheckConfigDao avayaParametersTelnetBcmsVdnCheckCfgDao;

    @Resource(name = "avayaParametersTelnetBcmsTrunkCheckCfgDao")
    private iCheckConfigDao avayaParametersTelnetBcmsTrunkCheckCfgDao;

    @Resource(name = "avayaParametersTelnetLicenseUtilisationCheckCfgDao")
    private iCheckConfigDao avayaParametersTelnetLicenseUtilisationCheckCfgDao;

    /**
     * Autowired taskpool which periodically execute received tasks
     */
    @Resource(name = "taskPool")
    private TaskPool taskPool;

    /**
     *  Method for Schedule all configured tasks
     */
    public void pushAllTasks(){
        schedulePingAllSrv();
        scheduleSnmpMemUsageAllSrv();
        scheduleSnmpCpuLoadAllSrv();
        scheduleSnmpVpmsChannelsUsage();
        scheduleAcmTelnetBcmsVdnTask();
        scheduleAcmTelnetBcmsTrunkTask();
        scheduleAcmTelnetLicenseUtilisationTask();
    }

    /**
     * Method set config to task and schedule it in the task pool
     */
    private void pushTaskForCfg(AbstractTask task, CheckConfig checkConfig){
        task.setCheckCfg(checkConfig);
        taskPool.add(task);
        logger.info("Monitoring service add task for srv (id{})", checkConfig.getEntityId() );
     }

    public void schedulePingAllSrv(){
        for (CheckConfig checkCfg : serverPingConfigDao.getAll()) {
            pushTaskForCfg(getPingTask(), checkCfg);
        }
    }

    public void scheduleSnmpCpuLoadAllSrv(){
        for (CheckConfig checkCfg : serverCpuLoadCheckConfigDao.getAll()) {
            pushTaskForCfg(getSnmpCpuLoadTask(), checkCfg);
        }
    }

    public void scheduleSnmpMemUsageAllSrv(){
        for (CheckConfig checkCfg : serverMemLoadCheckConfigDao.getAll()) {
            pushTaskForCfg(getSnmpMemUsageTask(), checkCfg);
        }
    }

    public void scheduleSnmpVpmsChannelsUsage(){
        for (CheckConfig checkCfg : snmpVpmsChannelsUsageCheckCfgDao.getAll()) {
            pushTaskForCfg(getSnmpVpmsChannelsUsageTask(), checkCfg);
        }
    }

    public void scheduleAcmTelnetBcmsVdnTask(){
        for (CheckConfig checkCfg : avayaParametersTelnetBcmsVdnCheckCfgDao.getAll()) {
            pushTaskForCfg(getAcmTelnetBcmsVdnTask(), checkCfg);
        }
    }

    public void scheduleAcmTelnetBcmsVdn24hTask(){
        for (CheckConfig checkCfg : avayaParametersTelnetBcmsVdnCheckCfgDao.getAll()) {
            pushTaskForCfg(getAcmTelnetBcmsVdn24hTask(), checkCfg);
        }
    }

    public void scheduleAcmTelnetBcmsTrunkTask(){
        for (CheckConfig checkCfg : avayaParametersTelnetBcmsTrunkCheckCfgDao.getAll()) {
            pushTaskForCfg(getAcmTelnetBcmsTrunkTask(), checkCfg);
        }
    }

    public void scheduleAcmTelnetBcmsTrunk24hTask(){
        for (CheckConfig checkCfg : avayaParametersTelnetBcmsTrunkCheckCfgDao.getAll()) {
            pushTaskForCfg(getAcmTelnetBcmsTrunk24hTask(), checkCfg);
        }
    }

    public void scheduleAcmTelnetLicenseUtilisationTask(){
        for (CheckConfig checkCfg : avayaParametersTelnetLicenseUtilisationCheckCfgDao.getAll()) {
            pushTaskForCfg(getAcmTelnetLicenseUtilisationTask(), checkCfg);
        }
    }

    // Method for testing
    @Deprecated
    public void pushBeepers(int count){
        List<BeeperTask> beeperTaskList = new ArrayList<BeeperTask>();
        for (int i=0; i<count; i++){
            BeeperTask bt = new BeeperTask();
            bt.setId(i);
            bt.setPeriod(5000);
            beeperTaskList.add(bt);
        }
        for (BeeperTask beeperTask : beeperTaskList) {
            taskPool.add(beeperTask);
        }
    }



    /**
     *  Methods below must be replaced to lookup-method in the spring context
     *  Methods below return prepared task-instances which ready to work
     */
    public PingTask getPingTask(){
        return new PingTask();
    }
    public CpuLoadTask getSnmpCpuLoadTask(){
        return new CpuLoadTask();
    }
    public MemUsageTask getSnmpMemUsageTask(){
        return new MemUsageTask();
    }
    public VpmsChannelsUsageTask getSnmpVpmsChannelsUsageTask(){
        return new VpmsChannelsUsageTask();
    }
    public AcmTelnetBcmsVdnTask getAcmTelnetBcmsVdnTask(){
        return new AcmTelnetBcmsVdnTask();
    }
    public AcmTelnetBcmsVdn24hTask getAcmTelnetBcmsVdn24hTask(){
        return new AcmTelnetBcmsVdn24hTask();
    }
    public AcmTelnetBcmsTrunkTask getAcmTelnetBcmsTrunkTask(){
        return new AcmTelnetBcmsTrunkTask();
    }
    public AcmTelnetBcmsTrunk24hTask getAcmTelnetBcmsTrunk24hTask(){
        return new AcmTelnetBcmsTrunk24hTask();
    }
    public AcmTelnetLicenseUtilisation getAcmTelnetLicenseUtilisationTask(){
        return new AcmTelnetLicenseUtilisation();
    }


}


