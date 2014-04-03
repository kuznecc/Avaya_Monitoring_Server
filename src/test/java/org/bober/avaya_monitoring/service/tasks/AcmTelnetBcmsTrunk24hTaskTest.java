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
public class AcmTelnetBcmsTrunk24hTaskTest extends AbstractAcmTelnetTaskTest{

    private ArrayList<String> telnetOutputExample = new ArrayList<String>() {{
        add("list bcms trunk 2`7`list bcms trunk 2 `8`7` Page 1`8`BCMS TRUNK GROUP REPORT `Switch Name: `Date: 1:48 pm FRI MAR 21` 2014 `Group: 2 `Group Name: Trunk2 `Number of Trunks: ` INCOMING ` OUTGOING `ALL `TIME `TIME `CALLS ABAND TIME CCS`CALLS COMP TIME CCS`BUSY MAINT`13:00-14:00 ` 1445` 61` 3:37` 3135.19` 0` 0` 0:00` 0.00` 0` 0 `14:00-15:00 ` 1464` 64` 3:52` 3395.15` 5` 0` 0:23` 1.13` 0` 0 `15:00-16:00 ` 1459` 44` 3:27` 3017.91` 4` 0` 0:52` 2.07` 0` 0 `16:00-17:00 ` 1445` 55` 3:41` 3188.16` 4` 0` 0:46` 1.84` 0` 0 `17:00-18:00 ` 1248` 56` 3:33` 2656.31` 0` 0` 0:00` 0.00` 0` 0 `18:00-19:00 ` 1165` 37` 3:45` 2621.86` 0` 0` 0:00` 0.00` 0` 0 `19:00-20:00 ` 1184` 43` 4:04` 2890.03` 0` 0` 0:00` 0.00` 0` 0 `20:00-21:00 ` 1008` 30` 3:56` 2374.26` 0` 0` 0:00` 0.00` 0` 0 `21:00-22:00 ` 744` 26` 3:50` 1708.16` 0` 0` 0:00` 0.00` 0` 0`22:00-23:00 ` 385` 15` 3:59` 919.19` 0` 0` 0:00` 0.00` 0` 0 `7` press CANCEL to quit -- press NEXT PAGE to continue`8`");
        add("`7` Page 2`8`BCMS TRUNK GROUP REPORT `Switch Name: `Date: 1:48 pm FRI MAR 21` 2014 `Group: 2 `Group Name: Trunk2 `Number of Trunks: ` INCOMING ` OUTGOING `ALL `TIME `TIME `CALLS ABAND TIME CCS`CALLS COMP TIME CCS`BUSY MAINT`23:00- 0:00 ` 157` 15` 3:31` 331.33` 0` 0` 0:00` 0.00` 0` 0` 0:00- 1:00 ` 58` 2` 3:57` 137.67` 0` 0` 0:00` 0.00` 0` 0 ` 1:00- 2:00 ` 43` 1` 3:58` 102.55` 0` 0` 0:00` 0.00` 0` 0 ` 2:00- 3:00 ` 14` 1` 4:15` 35.72` 0` 0` 0:00` 0.00` 0` 0 ` 3:00- 4:00 ` 5` 0` 4:10` 12.49` 0` 0` 0:00` 0.00` 0` 0 ` 4:00- 5:00 ` 15` 2` 2:21` 21.13` 0` 0` 0:00` 0.00` 0` 0 ` 5:00- 6:00 ` 33` 1` 1:56` 38.31` 0` 0` 0:00` 0.00` 0` 0 ` 6:00- 7:00 ` 124` 10` 2:45` 204.74` 0` 0` 0:00` 0.00` 0` 0 ` 7:00- 8:00 ` 399` 35` 2:30` 600.24` 0` 0` 0:00` 0.00` 0` 0 ` 8:00- 9:00 ` 1033` 64` 2:59` 1846.67` 8` 0` 0:46` 3.69` 0` 0 `7` press CANCEL to quit -- press NEXT PAGE to continue`8`");
        add("`7` Page 3`8`BCMS TRUNK GROUP REPORT `Switch Name: `Date: 1:48 pm FRI MAR 21` 2014 `Group: 2 `Group Name: Trunk2 `Number of Trunks: ` INCOMING ` OUTGOING `ALL `TIME `TIME `CALLS ABAND TIME CCS`CALLS COMP TIME CCS`BUSY MAINT` 9:00-10:00 ` 1442` 49` 3:18` 2851.31` 4` 0` 0:39` 1.56` 0` 0 `10:00-11:00 ` 1428` 43` 3:50` 3277.28` 5` 0` 0:57` 2.84` 0` 0 `11:00-12:00 ` 1230` 32` 3:33` 2616.60` 8` 0` 0:56` 4.49` 0` 0 `12:00-13:00 ` 1149` 44` 3:24` 2339.31` 2` 0` 0:33` 0.66` 0` 0 `----------- `-----`-----`------`--------`-----`-----`------`--------`---`--- `SUMMARY `18677` 730` 3:36`40321.57` 40` 0` 0:46` 18.28` 0` 0 `7`Command successfully completed`8`7`8`Command: `7`8`Command:");
    }};

    private ArrayList<String> telnetOutputExampleOneRow = new ArrayList<String>() {{
        add("list bcms trunk 2`7`list bcms trunk 2 `8`7` Page 1`8`BCMS TRUNK GROUP REPORT `Switch Name: `Date: 4:07 pm FRI MAR 22` 2014 `Group: 2 `Group Name: Trunk2 `Number of Trunks: ` INCOMING ` OUTGOING `ALL `TIME `TIME `CALLS ABAND TIME CCS`CALLS COMP TIME CCS`BUSY MAINT`15:00-16:00 ` 11` 22` 0:00` 0.00` 265` 170` 7:25` 1178.88` 0` 0 `");
    }};

    List<CheckResult> checkResultsFromTelnetOutput = new ArrayList<CheckResult>(){{
        add(new CheckResult(new Date(1395410400000L),11,"4",11));
        add(new CheckResult(new Date(1395410400000L),12,"4",22));
        add(new CheckResult(new Date(1395410400000L),13,"4",265));
        add(new CheckResult(new Date(1395410400000L),14,"4",170));
    }};

    private final int EXPECTED_CHECK_RESULTS_COUNT = 96;

    @Test
    public void testGetCheckResultsFromTelnetOutput() throws Exception {

        checkConfigDummy.setAttributes("4");

        final AcmTelnetBcmsTrunk24hTask acmTelnetBcmsTrunk24hTask =
                new AcmTelnetBcmsTrunk24hTask() {{
                    checkConfig = checkConfigDummy;
                    setMonitoredEntity(monitoredEntityDummy);
                }};

        List<CheckResult> checkResults;

        /* NULL telnet output test */

        checkResults = acmTelnetBcmsTrunk24hTask.getCheckResultsFromTelnetOutput(null);

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Empty telnet output test */

        checkResults = acmTelnetBcmsTrunk24hTask.getCheckResultsFromTelnetOutput(new ArrayList<String>());

        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", 0, checkResults.size());

        /* Normally telnet output test */

        checkResults = acmTelnetBcmsTrunk24hTask.getCheckResultsFromTelnetOutput(telnetOutputExample);
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        assertEquals("Some result objects have null value statement", 0, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));

        /* Normally telnet output one row content test */
        checkResults = acmTelnetBcmsTrunk24hTask.getCheckResultsFromTelnetOutput(telnetOutputExampleOneRow);

        assertEquals("Wrong CheckResult objects in the list.", checkResultsFromTelnetOutput, checkResults);


    }
}
