package org.bober.avaya_monitoring.web.controller;

import org.bober.avaya_monitoring.service.MonitoringService;
import org.bober.avaya_monitoring.service.TaskPool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Restful сontroller for monitoring service control page
 */

@Controller
@RequestMapping("/service/")
public class ServiceController {
    @Resource(name = "monitoringService")
    MonitoringService service;

    @Resource(name = "taskPool")
    TaskPool pool;

    /**
     * Return view of service control page
     *
     * @return content of view 'servicePage'
     */
    @RequestMapping(value = "/getView",method = RequestMethod.GET)
    public String getView(){
        return "servicePage";
    }

    @ResponseBody
    @RequestMapping(value = "/pushAllTasks",method = RequestMethod.GET)
    public String pushAllTasks(){
        service.pushAllTasks();
        return "All configured tasks are scheduled";
    }

    @ResponseBody
    @RequestMapping(value = "/status",method = RequestMethod.GET)
    public String returnServiceStatus(){
        StringBuffer sb = new StringBuffer();

        sb.append("_Service status_<br>");

        Map<String,String> status = pool.getStatus();
        for (String key : status.keySet()) {
            sb.append(key + "  :  " + status.get(key) + "<br>");
        }

        return sb.toString();
    }

    @RequestMapping(value = "/tasksStatus",method = RequestMethod.GET)
    public String returnTasksStatus(ModelMap model){
        model.addAttribute( "entityList", pool.getTasksStatus() );

        return "abstractEntityToTable";
    }

    @ResponseBody
    @RequestMapping(value = "/cancelAllTasks",method = RequestMethod.GET)
    public String cancelAllTasks(){
        pool.cancelAllTasks();
        return "all tasks are canceled";
    }

    @ResponseBody
    @RequestMapping(value = "/shutdown",method = RequestMethod.GET)
    public String shutdownService(){
        pool.shutdown();
        return "pool shutdown";
    }

    @ResponseBody
    @RequestMapping(value = "/purge",method = RequestMethod.GET)
    public String purgeService(){
        pool.shutdown();
        return "pool purge";
    }

    @ResponseBody
    @RequestMapping(value = "/pushTask/{taskName}",method = RequestMethod.GET)
    public String returnTaskList(@PathVariable String taskName){
        if (taskName.equals("ping")) {
            service.schedulePingAllSrv();
            return "Ping tasks pushed";
        }
        if (taskName.equals("cpu")) {
            service.scheduleSnmpCpuLoadAllSrv();
            return "CPU check tasks pushed";
        }
        if (taskName.equals("mem")) {
            service.scheduleSnmpMemUsageAllSrv();
            return "MEM check tasks pushed";
        }
        if (taskName.equals("vpUsage")) {
            service.scheduleSnmpVpmsChannelsUsage();
            return "vpUsage check tasks pushed";
        }
        if (taskName.equals("bcmsVdn")) {
            service.scheduleAcmTelnetBcmsVdnTask();
            return "bcmsVdn check tasks pushed";
        }
        if (taskName.equals("bcmsVdn24")) {
            service.scheduleAcmTelnetBcmsVdn24hTask();
            return "bcmsVdn24 check tasks pushed";
        }
        if (taskName.equals("bcmsTrunk")) {
            service.scheduleAcmTelnetBcmsTrunkTask();
            return "bcmsTrunk check tasks pushed";
        }
        if (taskName.equals("bcmsTrunk24")) {
            service.scheduleAcmTelnetBcmsTrunk24hTask();
            return "bcmsTrunk24 check tasks pushed";
        }
        if (taskName.equals("acmLicUtil")) {
            service.scheduleAcmTelnetLicenseUtilisationTask();
            return "ACM license utilisation check tasks pushed";
        }

        return "unknown task";
    }

}
