package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 */
public class AcmTelnetBcmsVdn24hTaskTest extends AbstractAcmTelnetTaskTest {

    private ArrayList<String> telnetOutputExample = new ArrayList<String>() {{
        add("list bcms vdn 98200`7`list bcms vdn 98200 `8`7` Page 1`8`BCMS VECTOR DIRECTORY NUMBER REPORT `Switch Name: `Date: 9:22 pm WED MAR 19` 2014 `VDN: 98200 `VDN Name: AIC Front End RR `Acceptable Service Level: 30 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `TIME `OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL`21:00-22:00 ` 735` 0` 0:00` 12` 0:01` 0:00` 0` 723` 0` 0`22:00-23:00 ` 347` 0` 0:00` 7` 0:02` 0:00` 0` 340` 0` 0`23:00- 0:00 ` 184` 0` 0:00` 2` 0:01` 0:00` 0` 182` 0` 0` 0:00- 1:00 ` 94` 0` 0:00` 4` 0:02` 0:00` 0` 90` 0` 0` 1:00- 2:00 ` 26` 0` 0:00` 0` 0:00` 0:00` 0` 26` 0` 0` 2:00- 3:00 ` 19` 0` 0:00` 0` 0:00` 0:00` 0` 19` 0` 0` 3:00- 4:00 ` 10` 0` 0:00` 0` 0:00` 0:00` 0` 10` 0` 0` 4:00- 5:00 ` 12` 0` 0:00` 1` 0:04` 0:00` 0` 11` 0` 0` 5:00- 6:00 ` 37` 0` 0:00` 2` 0:02` 0:00` 0` 35` 0` 0 `7` press CANCEL to quit -- press NEXT PAGE to continue`8`");
        add("`7` Page 2`8`BCMS VECTOR DIRECTORY NUMBER REPORT `Switch Name: `Date: 9:22 pm WED MAR 19` 2014 `VDN: 98200 `VDN Name: AIC Front End RR `Acceptable Service Level: 30 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `TIME `OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL` 6:00- 7:00 ` 121` 0` 0:00` 2` 0:03` 0:00` 0` 119` 0` 0` 7:00- 8:00 ` 327` 0` 0:00` 5` 0:02` 0:00` 0` 322` 0` 0` 8:00- 9:00 ` 979` 0` 0:00` 12` 0:02` 0:00` 0` 967` 0` 0` 9:00-10:00 ` 1524` 0` 0:00` 13` 0:02` 0:00` 0` 1511` 0` 0`10:00-11:00 ` 1651` 0` 0:00` 14` 0:02` 0:00` 0` 1637` 0` 0`11:00-12:00 ` 1540` 0` 0:00` 18` 0:01` 0:00` 0` 1522` 0` 0`12:00-13:00 ` 1414` 0` 0:00` 18` 0:02` 0:00` 0` 1396` 0` 0`13:00-14:00 ` 1359` 0` 0:00` 21` 0:02` 0:00` 0` 1338` 0` 0`14:00-15:00 ` 1549` 0` 0:00` 20` 0:02` 0:00` 0` 1529` 0` 0 `7` press CANCEL to quit -- press NEXT PAGE to continue`8`");
        add("`7` Page 3`8`BCMS VECTOR DIRECTORY NUMBER REPORT `Switch Name: `Date: 9:22 pm WED MAR 19` 2014 `VDN: 98200 `VDN Name: AIC Front End RR `Acceptable Service Level: 30 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `TIME `OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL`15:00-16:00 ` 1417` 0` 0:00` 21` 0:02` 0:00` 0` 1396` 0` 0`16:00-17:00 ` 1284` 0` 0:00` 23` 0:02` 0:00` 0` 1261` 0` 0`17:00-18:00 ` 1239` 0` 0:00` 12` 0:02` 0:00` 0` 1227` 0` 0`18:00-19:00 ` 1138` 0` 0:00` 15` 0:02` 0:00` 0` 1123` 0` 0`19:00-20:00 ` 1102` 0` 0:00` 13` 0:02` 0:00` 0` 1089` 0` 0`20:00-21:00 ` 1078` 0` 0:00` 20` 0:02` 0:00` 0` 1058` 0` 0 `----------- `------`-----`-----`-----`-----`-----`------`-----`-----`---`SUMMARY ` 19186` 0` 0:00` 255` 0:01` 0:00` 0`18931` 0` 0 `7`Command successfully completed`8`7`8`Command: `7`8`Command:");
    }};

    private ArrayList<String> telnetOutputExampleOneRow = new ArrayList<String>() {{
        add("list bcms vdn 98200`7`list bcms vdn 98200 `8`7` Page 1`8`BCMS VECTOR DIRECTORY NUMBER REPORT `Switch Name: `Date: 9:22 pm WED MAR 19` 2014 `VDN: 98200 `VDN Name: AIC Front End RR `Acceptable Service Level: 30 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `TIME `OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL`21:00-22:00 ` 735` 0` 0:00` 12` 0:01` 0:00` 0` 723` 0` 0`");
    }};

    List<CheckResult> checkResultsFromTelnetOutput = new ArrayList<CheckResult>(){{
        add(new CheckResult(new Date(1395172800000L),5,"98200",735));
        add(new CheckResult(new Date(1395172800000L),6,"98200",0));
        add(new CheckResult(new Date(1395172800000L),7,"98200",12));
        add(new CheckResult(new Date(1395172800000L),8,"98200",723));
    }};

    private ArrayList<String> telnetOutputExampleBadDate = new ArrayList<String>() {{
        add("list bcms vdn 98200`7`list bcms vdn 98200 `8`7` Page 1`8`BCMS VECTOR DIRECTORY NUMBER REPORT `Switch Name: `Date: 9:22 pm WED M_R 19` 2014 `VDN: 98200 `VDN Name: AIC Front End RR `Acceptable Service Level: 30 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `TIME `OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL`21:00-22:00 ` 735` 0` 0:00` 12` 0:01` 0:00` 0` 723` 0` 0`22:00-23:00 ` 347` 0` 0:00` 7` 0:02` 0:00` 0` 340` 0` 0`23:00- 0:00 ` 184` 0` 0:00` 2` 0:01` 0:00` 0` 182` 0` 0` 0:00- 1:00 ` 94` 0` 0:00` 4` 0:02` 0:00` 0` 90` 0` 0` 1:00- 2:00 ` 26` 0` 0:00` 0` 0:00` 0:00` 0` 26` 0` 0` 2:00- 3:00 ` 19` 0` 0:00` 0` 0:00` 0:00` 0` 19` 0` 0` 3:00- 4:00 ` 10` 0` 0:00` 0` 0:00` 0:00` 0` 10` 0` 0` 4:00- 5:00 ` 12` 0` 0:00` 1` 0:04` 0:00` 0` 11` 0` 0` 5:00- 6:00 ` 37` 0` 0:00` 2` 0:02` 0:00` 0` 35` 0` 0 `7` press CANCEL to quit -- press NEXT PAGE to continue`8`");
        add("`7` Page 2`8`BCMS VECTOR DIRECTORY NUMBER REPORT `Switch Name: `Date: 9:22 pm WED MAR 19` 2014 `VDN: 98200 `VDN Name: AIC Front End RR `Acceptable Service Level: 30 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `TIME `OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL` 6:00- 7:00 ` 121` 0` 0:00` 2` 0:03` 0:00` 0` 119` 0` 0` 7:00- 8:00 ` 327` 0` 0:00` 5` 0:02` 0:00` 0` 322` 0` 0` 8:00- 9:00 ` 979` 0` 0:00` 12` 0:02` 0:00` 0` 967` 0` 0` 9:00-10:00 ` 1524` 0` 0:00` 13` 0:02` 0:00` 0` 1511` 0` 0`10:00-11:00 ` 1651` 0` 0:00` 14` 0:02` 0:00` 0` 1637` 0` 0`11:00-12:00 ` 1540` 0` 0:00` 18` 0:01` 0:00` 0` 1522` 0` 0`12:00-13:00 ` 1414` 0` 0:00` 18` 0:02` 0:00` 0` 1396` 0` 0`13:00-14:00 ` 1359` 0` 0:00` 21` 0:02` 0:00` 0` 1338` 0` 0`14:00-15:00 ` 1549` 0` 0:00` 20` 0:02` 0:00` 0` 1529` 0` 0 `7` press CANCEL to quit -- press NEXT PAGE to continue`8`");
        add("`7` Page 3`8`BCMS VECTOR DIRECTORY NUMBER REPORT `Switch Name: `Date: 9:22 pm WED MAR 19` 2014 `VDN: 98200 `VDN Name: AIC Front End RR `Acceptable Service Level: 30 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `TIME `OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL`15:00-16:00 ` 1417` 0` 0:00` 21` 0:02` 0:00` 0` 1396` 0` 0`16:00-17:00 ` 1284` 0` 0:00` 23` 0:02` 0:00` 0` 1261` 0` 0`17:00-18:00 ` 1239` 0` 0:00` 12` 0:02` 0:00` 0` 1227` 0` 0`18:00-19:00 ` 1138` 0` 0:00` 15` 0:02` 0:00` 0` 1123` 0` 0`19:00-20:00 ` 1102` 0` 0:00` 13` 0:02` 0:00` 0` 1089` 0` 0`20:00-21:00 ` 1078` 0` 0:00` 20` 0:02` 0:00` 0` 1058` 0` 0 `----------- `------`-----`-----`-----`-----`-----`------`-----`-----`---`SUMMARY ` 19186` 0` 0:00` 255` 0:01` 0:00` 0`18931` 0` 0 `7`Command successfully completed`8`7`8`Command: `7`8`Command:");
    }};


    private final int EXPECTED_CHECK_RESULTS_COUNT = 96;


    @Test
    public void testGetCheckResultsFromTelnetOutput() throws Exception {

        checkConfigDummy.setAttributes("98200");

        final AcmTelnetBcmsVdn24hTask acmTelnetBcmsVdn24Task =
                new AcmTelnetBcmsVdn24hTask() {{
                    checkConfig = checkConfigDummy;
                    setMonitoredEntity(monitoredEntityDummy);
                }};

        List<CheckResult> checkResults;

        /* NULL telnet output test */

        checkResults = acmTelnetBcmsVdn24Task.getCheckResultsFromTelnetOutput(null);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Empty telnet output test */

        checkResults = acmTelnetBcmsVdn24Task.getCheckResultsFromTelnetOutput(new ArrayList<String>());

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Normally telnet output test */

        checkResults = acmTelnetBcmsVdn24Task.getCheckResultsFromTelnetOutput(telnetOutputExample);
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        assertEquals("Some result objects have null value statement", 0, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));

        /* Normally telnet output one row content test */
        checkResults = acmTelnetBcmsVdn24Task.getCheckResultsFromTelnetOutput(telnetOutputExampleOneRow);

        assertEquals("Wrong CheckResult objects in the list.", checkResultsFromTelnetOutput, checkResults);

    }
}
