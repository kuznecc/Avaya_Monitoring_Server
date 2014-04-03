package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.helper.MathHelper;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.service.tasks.util.ParserHelper;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.round;


/**
 * This monitoring task exeute command like
 * snmpwalk -Ovq -c COMMUNITY_READ -v 2c 10.7.2.254 .1.3.6.1.2.1.25.3.3.1.2
 * parse output and cpu load % of an server
 */
public class CpuLoadTask extends AbstractSnmpTask {

    {
        setMeasurementUnit("%");
    }

    @Override
    public String getDescription() {
        return String.format(
                "Этот таск определяет процент загрузки CPU сервера." +
                        "Проверка осуществляется за счет парсинга вывода программы snmpwalk для " +
                        "oid .1.3.6.1.2.1.25.3.3.1.2(win), которая возвращает процент утилизации " +
                        "кадого ядра процессора на сервере. Результатом таска является среднее " +
                        "арифметическое этих значений."
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
            snmpwalk -Ovq -c COMMUNITY_READ -v 2c 10.7.2.254 .1.3.6.1.2.1.25.3.3.1.2
                2
                0
                1
                2
        */

        /* Prepare command */

        /* specified oid for this task that will be joined with cmd-variable */
        String oid = ".1.3.6.1.2.1.25.3.3.1.2";

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

        Integer outputAllLinesSum = MathHelper.getListSum(outputLinesInInt);

        int result = round(
                outputAllLinesSum / (float) outputLinesInInt.size()
        );

        return getTaskResultList(result);
    }


}
