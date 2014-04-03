package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckConfig;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.service.tasks.util.AcmTelnetConnection;
import org.bober.avaya_monitoring.service.tasks.util.AcmTelnetScript;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This monitoring task make telnet-connection to the ACM server (getMonitoredEntityServer()),
 * execute command like 'list bcms summary vdn 98200', parse result
 * and return list of CheckResult instances with bcms vdn counters data.
 * If this task can't parse date or report counters value then it return empty CheckResult list.
 * <p/>
 * EntityId  -  Entity description from avaya_parameters table
 * id_5 - AvayaParameter.BcmsVdnCallsOffered
 * id_6 - AvayaParameter.BcmsVdnAcdCalls
 * id_7 - AvayaParameter.BcmsVdnAbandCalls
 * id_8 - AvayaParameter.BcmsVdnFlowOut
 */
public class AcmTelnetBcmsVdnTask extends AbstractAcmTelnetTask {
    {
        setMeasurementUnit("");
    }


    /**
     * This method create AcmTelnetScript, execute it in telnet session, parse output and return list of CheckResult
     *
     * @return list of CheckResult instances for specified entity's
     */
    @Override
    protected List<CheckResult> childTaskRunLogic() {

        /* Prepare acm telnet script */

        Integer vdn;

        try {
            vdn = Integer.parseInt(getCheckConfig().getAttributes());
        } catch (Exception e) {
            logAnError("Can't parse vdn number from CheckConfig object attributes", e);
            return null;
        }

        AcmTelnetScript acmScript =
                new AcmTelnetScript(getAcmTelnetConnection(), "list bcms summary vdn " + vdn) {
                    @Override
                    public List<String> call() throws Exception {
                        executeCommand();
                        return Arrays.asList(connection.readUntil("Command:"));
                    }
                };


        /* Execute acm telnet script */

        List<String> telnetOutput;
        try {
            telnetOutput = getAcmTelnetConnection().executeAvayaTelnetCmd(acmScript);
        } catch (Exception e) {
            logAnError("Can't execute prepared AcmTelnetScript over TelnetConnection class", e);
            return null;
        }

        return getCheckResultsFromTelnetOutput(telnetOutput);
    }

    /**
     * This method parse received telnet-output for specified bcms vdn counters
     * and return it like a CheckResult objects list.
     *
     * @param telnetOutput - output from telnet session, which was executed AcmTelnetScript
     * @return list of CheckResult instances for specified entity's.
     */
    List<CheckResult> getCheckResultsFromTelnetOutput(List<String> telnetOutput) {
        List<CheckResult> result = new ArrayList<>();

        if (telnetOutput == null || telnetOutput.isEmpty()) return result;

        StringBuilder telnetOutputSb = new StringBuilder();

            for (String telnetOutputRow : telnetOutput) {
                telnetOutputSb.append(telnetOutputRow);
            }



        /* Parsing date of report */

        Date reportDate;
        try {
            /* Searching telnetOutput for string like "`Date: 8:49 am FRI MAR 21` 2014 `Time:` 7:00- 8:00" */
            List<String> parsedDateParts =
                    parseTelnetOutputByRegexp(
                            telnetOutputSb,
                            "`Date:\\s?\\d+:\\d+ \\w{2} \\w{3} (\\w{3}) (\\d+)`\\s?(\\d+)\\s?`Time:`\\s?\\d+:\\d+-\\s?(\\d+:\\d+)",
                            4
                    );

            /* In this variable we will try create datetime-string like "12:00 MAR 18 2014" */
            String parsedReportDate = parsedDateParts.get(3) + " " + parsedDateParts.get(0) + " " +
                    parsedDateParts.get(1) + " " + parsedDateParts.get(2);

            /* Date parser for strings like "12:00 MAR 18 2014" */
            SimpleDateFormat parserSDF = new SimpleDateFormat("HH:mm MMM dd yyyy", Locale.ENGLISH);
            reportDate = parserSDF.parse(parsedReportDate);

        } catch (Exception e) {
            logAnError("Can't parse date & time from telnet output.", e);
            return result;
        }


        /* Parsing report summary data from telnet output */

        try {
            List<CheckResult> preparedResult = new ArrayList<>();

            final List<String> parsedReportParts =
                    parseTelnetOutputByRegexp(
                            telnetOutputSb,
                            "`SUMMARY ` (\\d+)` (\\d+)`\\s?\\d+:\\d+` (\\d+)`\\s?\\d+:\\d+`\\s?\\d+:\\d+` \\d+` (\\d+)` \\d+`\\s?\\d+\\s?`",
                            4
                    );

            preparedResult.addAll(
                    Arrays.asList(
                            new CheckResult()
                                    .setEntityId(5)
                                    .setValue(Integer.parseInt(parsedReportParts.get(0))),
                            new CheckResult()
                                    .setEntityId(6)
                                    .setValue(Integer.parseInt(parsedReportParts.get(1))),
                            new CheckResult()
                                    .setEntityId(7)
                                    .setValue(Integer.parseInt(parsedReportParts.get(2))),
                            new CheckResult()
                                    .setEntityId(8)
                                    .setValue(Integer.parseInt(parsedReportParts.get(3)))
                    )
            );

            for (CheckResult checkResult : preparedResult) {
                checkResult.setDate(reportDate);
                checkResult.setAttributes(getCheckConfig().getAttributes());
            }

            result.addAll(preparedResult);
        } catch (Exception e) {
            logAnError("Can't parse report summary data from telnet output.", e);
        }

        return result;
    }



    /**
     * Stand alone integration test of this task
     */
    public static void main(String[] args) {

        final AcmTelnetConnection connection = new AcmTelnetConnection(){{
            setTelnetServerIp("10.7.2.33");
            setTelnetServerPort(5023);
            setTelnetLogin("ayudin");
            setTelnetPassword("******");
        }};


        final AcmTelnetBcmsVdnTask acmTelnetBcmsVdnTask = new AcmTelnetBcmsVdnTask() {{
            setAcmTelnetConnection( connection );
            checkConfig =
                    new CheckConfig() {{
                        setAttributes("98200");
                    }};
        }};


        long startTime = System.nanoTime();

        /* Receive telnet output from ACM server */
        final List<CheckResult> checkResults = acmTelnetBcmsVdnTask.childTaskRunLogic();


        long execTime = System.nanoTime() - startTime;

        System.out.println("Execution time : " + execTime + "nanoseconds");

        for (CheckResult checkResult : checkResults) {
            System.out.println(checkResult);
        }
    }
}
