package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.helper.MathHelper;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.service.tasks.util.ParserHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * This monitoring task exeute commands like
 * snmpwalk -Ovq -c PUBLIC_COMMON -v 2c 10.7.1.62 1.3.6.1.4.1.6889.2.17.3.1.1.14
 * parse output, calculate and return count of used channels on all MPP-servers
 */
public class VpmsChannelsUsageTask extends AbstractSnmpTask {
    {
        setMeasurementUnit("units");
    }

    @Override
    public String getDescription(){
        return String.format(
                "Этот таск определяет количество одновременно задействованных каналов " +
                        "на всех серверах VPMP. Для этого с помощью программы snmpwalk отправляется " +
                        "запрос на oid 1.3.6.1.4.1.6889.2.17.3.1.1.14 сервера vpms. Сервер" +
                        "нам возвращает значения утилизации каналов по каждому из серверов mpp." +
                        "Результатом таска является сумма всех полученных значений."
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
        /* Cmd example:
            snmpwalk -Ovq -c PUBLIC_COMMON -v 2c 10.7.1.62 1.3.6.1.4.1.6889.2.17.3.1.1.14
                9
                6
                4
                8
                3
        */

        /* Prepare command
        *   This OID return count of channels that used on each mpp-servers at this moment
        */

        String oid = "1.3.6.1.4.1.6889.2.17.3.1.1.14";

        // execute command
        String cmd = getSnmpCommand() + oid;

        return getCheckResultsFromCmdCommand(cmd);
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
