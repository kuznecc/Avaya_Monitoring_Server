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
public class AcmTelnetBcmsTrunkTaskTest extends AbstractAcmTelnetTaskTest {

    private ArrayList<String> telnetOutputExample = new ArrayList<String>() {{
        add("list bcms summary trunk 4`7`list bcms summary trunk 4 `8`7` Page 1`8`BCMS TRUNK GROUP SUMMARY REPORT `Switch Name: `Date: 4:07 pm FRI MAR 21` 2014 `Time:`15:00-16:00 ` INCOMING ` OUTGOING `ALL `TIME `GROUP NAME `CALLS ABAND TIME CCS`CALLS COMP TIME CCS`BUSY MAINT `SES-4 ` 0` 0` 0:00` 0.00` 265` 170` 7:25` 1178.88` 0` 0 `----------- `-----`-----`------`--------`-----`-----`------`--------`---`--- `SUMMARY ` 11` 22` 0:00` 0.00` 265` 170` 7:25` 1178.88` 0` 0 `7`Command successfully completed`8`7`8`Command:");
    }};
    private ArrayList<CheckResult> checkResultsFromTelnetOutput = new ArrayList<CheckResult>(){{
        add(new CheckResult(new Date(1395410400000L),11,"4",11));
        add(new CheckResult(new Date(1395410400000L),12,"4",22));
        add(new CheckResult(new Date(1395410400000L),13,"4",265));
        add(new CheckResult(new Date(1395410400000L),14,"4",170));
    }};

    private ArrayList<String> telnetOutputExampleBadDate = new ArrayList<String>() {{
        add("list bcms summary trunk 4`7`list bcms summary trunk 4 `8`7` Page 1`8`BCMS TRUNK GROUP SUMMARY REPORT `Switch Name: `Date: 4:07 pm FRI M_R 21` 2014 `Time:`15:00-16:00 ` INCOMING ` OUTGOING `ALL `TIME `GROUP NAME `CALLS ABAND TIME CCS`CALLS COMP TIME CCS`BUSY MAINT `SES-4 ` 0` 0` 0:00` 0.00` 265` 170` 7:25` 1178.88` 0` 0 `----------- `-----`-----`------`--------`-----`-----`------`--------`---`--- `SUMMARY ` 11` 22` 0:00` 0.00` 265` 170` 7:25` 1178.88` 0` 0 `7`Command successfully completed`8`7`8`Command:");
    }};

    private ArrayList<String> telnetOutputExampleBadTrunkCountersData = new ArrayList<String>() {{
        add("list bcms summary trunk 4`7`list bcms summary trunk 4 `8`7` Page 1`8`BCMS TRUNK GROUP SUMMARY REPORT `Switch Name: `Date: 4:07 pm FRI MAR 21` 2014 `Time:`15:00-16:00 ` INCOMING ` OUTGOING `ALL `TIME `GROUP NAME `CALLS ABAND TIME CCS`CALLS COMP TIME CCS`BUSY MAINT `SES-4 ` 0` 0` 0:00` 0.00` 265` 170` 7:25` 1178.88` 0` 0 `----------- `-----`-----`------`--------`-----`-----`------`--------`---`--- `SUMMARY ` 11` ` 0:00` 0.00` 265` 170` 7:25` 1178.88` 0` 0 `7`Command successfully completed`8`7`8`Command:");
    }};

    private final int EXPECTED_CHECK_RESULTS_COUNT = 4;

    @Test
    public void testGetCheckResultsFromTelnetOutput() throws Exception {

        checkConfigDummy.setAttributes("4");

        final AcmTelnetBcmsTrunkTask acmTelnetBcmsTrunkTask =
                new AcmTelnetBcmsTrunkTask() {{
                    checkConfig = checkConfigDummy;
                    setMonitoredEntity(monitoredEntityDummy);
                }};

        List<CheckResult> checkResults;

        /* NULL telnet output test */

        checkResults = acmTelnetBcmsTrunkTask.getCheckResultsFromTelnetOutput(null);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Empty telnet output test */

        checkResults = acmTelnetBcmsTrunkTask.getCheckResultsFromTelnetOutput(new ArrayList<String>());

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Normally telnet output test */

        checkResults = acmTelnetBcmsTrunkTask.getCheckResultsFromTelnetOutput(telnetOutputExample);
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        assertEquals("Wrong CheckResult objects in the list.", checkResultsFromTelnetOutput, checkResults);

        assertEquals("Some result objects have null value statement", 0, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));

        /* Bad date telnet output test */

        checkResults = acmTelnetBcmsTrunkTask.getCheckResultsFromTelnetOutput(telnetOutputExampleBadDate);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Bad vdn counters data telnet output test */

        checkResults = acmTelnetBcmsTrunkTask.getCheckResultsFromTelnetOutput(telnetOutputExampleBadTrunkCountersData);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());
    }
}
