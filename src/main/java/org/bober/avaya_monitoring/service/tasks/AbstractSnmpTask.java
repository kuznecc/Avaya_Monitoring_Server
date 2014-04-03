package org.bober.avaya_monitoring.service.tasks;


import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.entity.Server;
import org.bober.avaya_monitoring.service.tasks.util.CmdExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common class for all tasks which make snmp checks
 */
public abstract class AbstractSnmpTask extends AbstractTask {

    /**
     * Map of all CmdExecutor instances which have been created for specific entityId
     */
    private static Map<Integer, CmdExecutor> executorMap = new HashMap<>();

    /**
     * If CmdExecutor for entityId already created, then return it from map.
     * If CmdExecutor for entityIf have not yet created, then create new, add it to map, return it.
     *
     * @param entityId id of specified entity
     * @return stored CmdExecutor instance
     */
    protected static CmdExecutor getCmdExecutorForEntity(int entityId) {
        if (!executorMap.containsKey(entityId)) {

            synchronized (executorMap) {
                if (!executorMap.containsKey(entityId))
                    executorMap.put(entityId, new CmdExecutor());
            }

        }

        return executorMap.get(entityId);
    }


    /**
     * This method check received cmd-outputs for specified command
     * and return it like a CheckResult objects list.
     *
     * @param command - command, that will be executed
     * @return list of CheckResult instances for specified entity's.
     */
    protected List<CheckResult> getCheckResultsFromCmdCommand(String command) {

        CmdExecutor cmdResult = getCmdExecutorForEntity(getMonitoredEntity().getId());
        cmdResult.execute(command);

        List<String> errOutput = cmdResult.getErrOutput();
        List<String> stdOutput = cmdResult.getStdOutput();


        return getCheckResultsFromCmdOutputs(errOutput, stdOutput);
    }

    /**
     * This method check received cmd-outputs and return it like a CheckResult objects list.
     *
     * If output have errors in outputs then return empty List<CheckResult>
     *
     * @param stdOutput - stdOutput from cmd session
     * @param errOutput - errOutput from cmd session
     * @return list of CheckResult instances for specified entity's.
     */
    protected List<CheckResult> getCheckResultsFromCmdOutputs(List<String> errOutput, List<String> stdOutput) {

        if (errOutput!=null && errOutput.size() > 0) {
            logAnError(
                    String.format("ERR output:%s", errOutput)
            );
        }

        if (stdOutput==null || stdOutput.size() < 1 ||
                errOutput == null || errOutput.size() > 0) {
            return new ArrayList<>();
        }

        return getCheckResultsFromCmdStdOutput(stdOutput);
    }

    /**
     * This method parse received cmd-output for specified snmp-command
     * and return it like a CheckResult objects list.
     *
     * @param stdOutput - stdOutput from cmd session
     * @return list of CheckResult instances for specified entity's.
     */
    protected abstract List<CheckResult> getCheckResultsFromCmdStdOutput(List<String> stdOutput);

    /**
     * Return first part for all snmp commands
     */
    protected String getSnmpCommand() {
        Server server = (Server) getMonitoredEntity();
        return "snmpwalk -Ovq -c " + server.getSnmpCommunity() +
                " -v 2c " + server.getIp() + " ";
    }

    protected String getSnmpTimeoutOutputPattern() {
        return "Timeout: No Response from";
    }

}
