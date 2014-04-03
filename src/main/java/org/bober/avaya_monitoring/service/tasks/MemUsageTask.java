package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.helper.MathHelper;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.service.tasks.util.ParserHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * This monitoring task exeute commands like
 * snmpwalk -Ovq -c COMMUNITY_READ -v 2c 10.7.2.254 .1.3.6.1.2.1.25.5.1.1.2
 * snmpwalk -Ovq -c COMMUNITY_READ -v 2c 10.7.2.254 .1.3.6.1.2.1.25.2.2.0
 * parse output, calculate and MEM load % of an server
 */
public class MemUsageTask extends AbstractSnmpTask {
    {
        setMeasurementUnit("%");
    }

    @Override
    public String getDescription() {
        return String.format(
                "Этот таск проверяет какой процент загрузки оперативной памяти на сервере." +
                        "Проверка осуществляется за счет парсинга вывода программы snmpwalk для " +
                        "oid .1.3.6.1.2.1.25.5.1.1.2(win)(используемая память для каждого процесса на сервере)," +
                        "oid .1.3.6.1.2.1.25.2.2.0(win)(общая память, установленная на сервере) и последующего " +
                        "деления полученных результатов."
        );
    }

    /**
     * This method prepare command string, execute it in new instance of CmdExecutor,
     * check output, parse it and return list of CheckResult
     *
     * @return list of CheckResult instances for specified entity's
     */
    @Override
    protected List<CheckResult> childTaskRunLogic() {
        /* Example of command for this task :
        snmpwalk -Ovq -c COMMUNITY_READ -v 2c 10.7.2.254 .1.3.6.1.2.1.25.5.1.1.2
            16 KBytes
            244 KBytes
            5500 KBytes
            5212 KBytes
            4080 KBytes
            452 KBytes
            1712 KBytes
            4752 KBytes
        snmpwalk -Ovq -c COMMUNITY_READ -v 2c 10.7.2.254 .1.3.6.1.2.1.25.2.2.0
            3407100 KBytes
        */

        /* Preparing command for snmp request for used memory in KBytes */
        String usedMemOid = ".1.3.6.1.2.1.25.5.1.1.2";
        String cmd = getSnmpCommand() + usedMemOid;

        CheckResult usedMemSum = getCheckResultsFromCmdCommand(cmd).get(0);

        /* Preparing command for snmp request for total memory in KBytes */
        String totalMemOid = ".1.3.6.1.2.1.25.2.2.0";
        cmd = getSnmpCommand() + totalMemOid;

        CheckResult totalMem = getCheckResultsFromCmdCommand(cmd).get(0);


        return
                getTaskResultList(
                        getUsedMemoryPercent(usedMemSum, totalMem)
                );
    }

    protected int getUsedMemoryPercent(CheckResult usedMemSum, CheckResult totalMem) {
        int usedMemPercent = Math.round(
                usedMemSum.getValue() / (totalMem.getValue() / (float) 100)
        );

        return usedMemPercent;
    }

    /**
     * This method parse received cmd-output for specified snmp-command
     * and return it like a CheckResult objects list.
     *
     * @param stdOutput - stdOutput from cmd session
     * @return list of CheckResult instances for specified entity's.
     */
    @Override
    protected List<CheckResult> getCheckResultsFromCmdStdOutput(List<String> stdOutput) {
        if (stdOutput == null || stdOutput.isEmpty())
            return new ArrayList<>();

        List<Integer> outputLinesInInt = ParserHelper.parseStringListToInt(stdOutput);

        Integer result = MathHelper.getListSum(outputLinesInInt);

        return getTaskResultList(result);
    }

}
