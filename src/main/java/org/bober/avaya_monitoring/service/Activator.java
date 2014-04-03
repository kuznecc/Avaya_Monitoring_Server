package org.bober.avaya_monitoring.service;

import org.bober.avaya_monitoring.model.dao.impl.ConfigTableDaoJdbc;
import org.bober.avaya_monitoring.model.dao.impl.EntityAvayaParameterDaoJdbc;
import org.bober.avaya_monitoring.model.dao.impl.EntityServerDaoJdbc;
import org.bober.avaya_monitoring.model.dao.impl.ResultTableDaoJdbc;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Test class for running monitoring service without web-interface
 */
public class Activator {

    @Deprecated
    public static void main(String[] args) {

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("ServiceActivatorCtx.xml");

        MonitoringService service = (MonitoringService)
                ctx.getBean("monitoringService" );

        EntityServerDaoJdbc serverDaoJdbc = (EntityServerDaoJdbc) ctx.getBean("serverDao");
        EntityAvayaParameterDaoJdbc avayaParameterDaoJdbc = (EntityAvayaParameterDaoJdbc) ctx.getBean("avayaParameterDao");
        ResultTableDaoJdbc resultTableDaoJdbc = (ResultTableDaoJdbc) ctx.getBean("snmpVpmsChannelsUsageDao");
        ConfigTableDaoJdbc configTableDaoJdbc = (ConfigTableDaoJdbc) ctx.getBean("snmpVpmsChannelsUsageCheckCfgDao");

        // чтобы вывод программы не смешивался с системным выводом при старте спринга
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }


        /* Run service */
        System.out.println(" - Monitoring Service was started -" );


//        ArrayList<Server> list = new ArrayList<Server>() {{
//            add(new Server("test1","111"));
//            add(new Server("test2","222"));
//        }};
//        serverDaoJdbc.create(list);
//        serverDaoJdbc.delete( new Server(){{setId(17);}} );
//        serverDaoJdbc.update(new Server("ddd","333"){{setId(17);setOsType(Server.OS_TYPES.get(1));}});
//        ArrayList<AvayaParameter> list = new ArrayList<AvayaParameter>() {{
//            add(new AvayaParameter("test1",1));
//            add(new AvayaParameter("test2",1));
//        }};
//        avayaParameterDaoJdbc.create(list);
//        avayaParameterDaoJdbc.delete( new AvayaParameter(){{setId(4);}} );
//        avayaParameterDaoJdbc.update( new AvayaParameter("ddd",3){{setId(4);setSubSystem("acm");}});
//        ArrayList<CheckResult> list = new ArrayList<CheckResult>() {{
//            add(new CheckResult(){{setDate( new Date());setEntityId(1);setValue(999);}});
//            add(new CheckResult(){{setDate( new Date());setEntityId(1);setValue(888);}});
//        }};
//        resultTableDaoJdbc.create(list);
//        resultTableDaoJdbc.delete( new CheckResult(){{setId(22075);}} );
//        resultTableDaoJdbc.update(new CheckResult(){{setId(22076);setEntityId(1);setValue(111);setDate(new Date());}});
//        ArrayList<CheckConfig> list = new ArrayList<CheckConfig>() {{
//            add(new CheckConfig() {{setEntityId(2);}});
//            add(new CheckConfig() {{setEntityId(3);}});
//        }};
//        configTableDaoJdbc.create(list);
//        configTableDaoJdbc.delete( new CheckConfig(){{setId(9);}} );
//        configTableDaoJdbc.update(new CheckConfig(){{setId(9);setEntityId(4);setFrequency(999);setDisabled(true);}});



        service.scheduleAcmTelnetBcmsVdnTask();
//        service.scheduleSnmpCpuLoadAllSrv();
//        service.scheduleSnmpMemUsageAllSrv();
//        service.schedulePingAllSrv();
//        service.scheduleSnmpVpmsChannelsUsage();
//        service.pushBeepers(3);
    }

}
