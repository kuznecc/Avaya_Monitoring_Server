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
public class AcmTelnetBcmsVdnTaskTest extends AbstractAcmTelnetTaskTest {

    private ArrayList<String> telnetOutputExample = new ArrayList<String>() {{
        add("list bcms summary vdn 98239`7`list bcms summary vdn 98239 `8`7` Page 1`8`BCMS VECTOR DIRECTORY NUMBER SUMMARY REPORT `Switch Name: `Date: 8:49 am FRI MAR 21` 2014 `Time:` 7:00- 8:00 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `VDN NAME OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL `IVR on CM ` 0` 0` 0:00` 0` 0:00` 0:00` 0` 0` 0`----------- `------`-----`-----`-----`-----`-----`------`-----`-----`--- `SUMMARY ` 11` 22` 0:00` 33` 0:00` 0:00` 0` 44` 0`7`Command successfully completed`8`7`8`Command:");
    }};
    private ArrayList<CheckResult> checkResultsFromTelnetOutput = new ArrayList<CheckResult>(){{
        add(new CheckResult(new Date(1395381600000L),5,"98200",11));
        add(new CheckResult(new Date(1395381600000L),6,"98200",22));
        add(new CheckResult(new Date(1395381600000L),7,"98200",33));
        add(new CheckResult(new Date(1395381600000L),8,"98200",44));
    }};

    private ArrayList<String> telnetOutputExampleBadDate = new ArrayList<String>() {{
        add("list bcms summary vdn 98239`7`list bcms summary vdn 98239 `8`7` Page 1`8`BCMS VECTOR DIRECTORY NUMBER SUMMARY REPORT `Switch Name: `Date: 8:49 am FRI MAR 21` 2014 `Time:` 7:00- :00 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `VDN NAME OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL `IVR on CM ` 0` 0` 0:00` 0` 0:00` 0:00` 0` 0` 0`----------- `------`-----`-----`-----`-----`-----`------`-----`-----`--- `SUMMARY ` 11` 22` 0:00` 33` 0:00` 0:00` 0` 44` 0`7`Command successfully completed`8`7`8`Command:");
    }};

    private ArrayList<String> telnetOutputExampleBadVdnCountersData = new ArrayList<String>() {{
        add("list bcms summary vdn 98239`7`list bcms summary vdn 98239 `8`7` Page 1`8`BCMS VECTOR DIRECTORY NUMBER SUMMARY REPORT `Switch Name: `Date: 8:49 am FRI MAR 21` 2014 `Time:` 7:00- 8:00 ` AVG AVG AVG CALLS ` IN `CALLS ACD SPEED ABAND ABAND TALK/ CONN FLOW BUSY/ SERV `VDN NAME OFFERED CALLS ANSW CALLS TIME HOLD CALLS OUT DISC LEVL `IVR on CM ` 0` 0` 0:00` 0` 0:00` 0:00` 0` 0` 0`----------- `------`-----`-----`-----`-----`-----`------`-----`-----`--- `SUMMARY ` 11` 22` 0:00` ");

    }};

    private final int EXPECTED_CHECK_RESULTS_COUNT = 4;

    @Test
    public void testGetCheckResultsFromTelnetOutput() throws Exception {

        checkConfigDummy.setAttributes("98200");

        final AcmTelnetBcmsVdnTask acmTelnetBcmsVdnTask =
                new AcmTelnetBcmsVdnTask() {{
                    checkConfig = checkConfigDummy;
                    setMonitoredEntity(monitoredEntityDummy);
                }};


        List<CheckResult> checkResults;

        /* NULL telnet output test */

        checkResults = acmTelnetBcmsVdnTask.getCheckResultsFromTelnetOutput(null);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Empty telnet output test */

        checkResults = acmTelnetBcmsVdnTask.getCheckResultsFromTelnetOutput(new ArrayList<String>());

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Normally telnet output test */

        checkResults = acmTelnetBcmsVdnTask.getCheckResultsFromTelnetOutput(telnetOutputExample);
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        assertEquals("Wrong CheckResult objects in the list.", checkResultsFromTelnetOutput, checkResults);

        assertEquals("Some result objects have null value statement", 0, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));

        /* Bad date telnet output test */

        checkResults = acmTelnetBcmsVdnTask.getCheckResultsFromTelnetOutput(telnetOutputExampleBadDate);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Bad vdn counters data telnet output test */

        checkResults = acmTelnetBcmsVdnTask.getCheckResultsFromTelnetOutput(telnetOutputExampleBadVdnCountersData);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

    }

}
