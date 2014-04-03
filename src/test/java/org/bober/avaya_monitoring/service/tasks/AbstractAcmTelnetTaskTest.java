package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.entity.CheckConfig;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for class AbstractAcmTelnetTask
 *
 * also - Common class for all tests
 */
public class AbstractAcmTelnetTaskTest {


    @Test
    public void getDateListTest() throws Exception {

        long testDateTemplate = 1395410400000L;

        List<Integer> testHoursList = new ArrayList<>(
                Arrays.asList(21,22,23,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
        );

        List<Long> rightMethodResult = new ArrayList<>(
                Arrays.asList(1395399600000L,1395403200000L,1395406800000L,1395410400000L,1395414000000L,
                        1395417600000L,1395421200000L,1395424800000L,1395428400000L,1395432000000L,1395435600000L,
                        1395439200000L,1395442800000L,1395446400000L,1395450000000L,1395453600000L,1395457200000L,
                        1395460800000L,1395464400000L,1395468000000L,1395471600000L,1395475200000L,1395478800000L,
                        1395482400000L)
        );

        final List<Long> methodResult = AbstractAcmTelnetTask.getDateList(testHoursList, testDateTemplate);

        assertEquals("returned list haven't right values",rightMethodResult,methodResult);

    }



    final CheckConfig checkConfigDummy = new CheckConfig();


    final AbstractMonitoredEntity monitoredEntityDummy =
            new AbstractMonitoredEntity() {
                @Override
                public String getPrepareName() {
                    return "Monitored entity dummy";
                }

                @Override
                public String toString() {
                    return null;
                }
            };


    protected int getCountOfNullValues(List<CheckResult> checkResults) {
        int count = 0;
        for (CheckResult checkResult : checkResults) {
            if (checkResult.getValue() == null) {
                count++;
            }
        }
        return count;
    }

    protected int getCountOfNullDate(List<CheckResult> checkResults) {
        int count = 0;
        for (CheckResult checkResult : checkResults) {
            if (checkResult.getDate() == null) {
                count++;
            }
        }
        return count;
    }
}
