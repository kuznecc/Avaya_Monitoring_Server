package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.entity.Server;
import org.bober.avaya_monitoring.service.tasks.util.CmdExecutor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 */
public class AbstractSnmpTaskTest {

    AbstractSnmpTask task = new AbstractSnmpTask() {
        {
            setMonitoredEntity(
                    new Server(){{
                        setId(1);
                    }}
            );
        }

        @Override
        protected List<CheckResult> getCheckResultsFromCmdStdOutput(List<String> stdOutput) {
            return new ArrayList<>();
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        protected List<CheckResult> childTaskRunLogic() {
            return null;
        }
    };

    @Test
    public void testGetCmdExecutorForEntity() throws Exception {
        CmdExecutor e1 = AbstractSnmpTask.getCmdExecutorForEntity(1);
        AbstractSnmpTask.getCmdExecutorForEntity(2);
        AbstractSnmpTask.getCmdExecutorForEntity(3);

        assertTrue("executors for one entity not same", e1== AbstractSnmpTask.getCmdExecutorForEntity(1));
        assertTrue("executors for different entity was same", e1!= AbstractSnmpTask.getCmdExecutorForEntity(2));
    }

    @Test
    public void testGetCheckResultsFromCmdOutputs(){

        List<String> nullList = null;
        List<String> emptyList = new ArrayList<>();
        List<String> normList = new ArrayList<>(Arrays.asList("aa","bb"));

        List<CheckResult> result;

        result = task.getCheckResultsFromCmdOutputs(nullList, normList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdOutputs(emptyList, normList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdOutputs(normList, nullList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdOutputs(normList, emptyList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdOutputs(nullList, nullList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdOutputs(emptyList, emptyList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());

        result = task.getCheckResultsFromCmdOutputs(normList, normList);
        assertTrue("Result must be empty List<CheckResult>", result.isEmpty());
    }

}
