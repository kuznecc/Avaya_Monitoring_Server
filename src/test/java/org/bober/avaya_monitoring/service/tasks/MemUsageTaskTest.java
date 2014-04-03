package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.entity.Server;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 */
public class MemUsageTaskTest {
    private MemUsageTask task = new MemUsageTask() {{
        setMonitoredEntity(
                new Server() {{
                    setId(1);
                }}
        );
    }};

    @Test
    public void testGetCheckResultsFromCmdStdOutput() throws Exception {

        List<String> nullList = null;
        List<String> emptyList = new ArrayList<>();
        List<String> normList =
                new ArrayList<>(
                        Arrays.asList("9", "6", "4", "8", "3")
                );
        CheckResult normResult = new CheckResult().setEntityId(1).setValue(30);


        List<CheckResult> result;

        result = task.getCheckResultsFromCmdStdOutput(nullList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdStdOutput(emptyList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdStdOutput(normList);
        normResult.setDate(result.get(0).getDate());
        assertTrue("Result must be empty List<CheckResult>", result.get(0).equals(normResult));
    }

    @Test
    public void testGetUsedMemoryPercent() throws Exception {

        CheckResult usedMem = new CheckResult().setValue(1_500_000);
        CheckResult totalMem = new CheckResult().setValue(10_000_000);
        int rightPrecent = 15;

        int usedMemPercent = task.getUsedMemoryPercent(usedMem, totalMem);

        assertTrue("Wrong calculated percent of used memory", rightPrecent==usedMemPercent);
    }
}
