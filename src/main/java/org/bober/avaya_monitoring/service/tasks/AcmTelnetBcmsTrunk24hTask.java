package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.service.tasks.util.AcmTelnetScript;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This monitoring task make telnet-connection to the ACM server (serverId), execute command
 * like 'list bcms trunk 2' and parse result.
 * Task got from server rows with data for last 24h per hour
 *
 * return for each row :
 *      id10-AvayaParameter.BcmsTrunk
 *      id11-AvayaParameter.BcmsTrunkInCalls
 *      id12-AvayaParameter.BcmsTrunkInAband
 *      id13-AvayaParameter.BcmsTrunkOutCalls
 *      id14-AvayaParameter.BcmsTrunkOutComp
 * If task can't connect or parse response then return null.
 */
public class AcmTelnetBcmsTrunk24hTask extends AbstractAcmTelnetTask{
    {
        setMeasurementUnit("calls");
    }

    /**
     * This method create AcmTelnetScript, execute it in telnet session, parse output and return list of CheckResult
     *
     * @return list of CheckResult instances for specified entity's
     */
    @Override
    protected List<CheckResult> childTaskRunLogic() {

        /* Prepare acm telnet script */

        Integer trunk;

        try {
            trunk = Integer.parseInt(getCheckConfig().getAttributes());
        } catch (Exception e) {
            logAnError("Can't parse vdn number from CheckConfig object attributes", e);
            return null;
        }

        AcmTelnetScript acmScript =
                new AcmTelnetScript(getAcmTelnetConnection(), "list bcms summary trunk " + trunk) {
                    @Override
                    public List<String> call() throws Exception {
                        connection.write(cmd);
                        List<String> result = new ArrayList<>();
                        result.add(connection.readUntil(3000));
                        connection.nextPage();
                        result.add(connection.readUntil(3000));
                        connection.nextPage();
                        result.add(connection.readUntil(3000));

                        return result;
                    }
                };


        /* Execute acm telnet script */

        List<String> telnetOutput = null;
        try {
            telnetOutput = getAcmTelnetConnection().executeAvayaTelnetCmd(acmScript);
        } catch (Exception e) {
            logAnError("Can't execute prepared AcmTelnetScript over TelnetConnection class", e);
            return null;
        }

        return getCheckResultsFromTelnetOutput(telnetOutput);
    }

    /**
     * This method parse received telnet-output for specified bcms trunk counters
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

        Date reportDate = null;
        try {
            /* Searching telnetOutput for string like "`Date: 9:22 pm WED MAR 19` 2014" */
            List<String> parsedDateParts =
                    parseTelnetOutputByRegexp(
                            telnetOutputSb,
                            "`Date: \\d+:\\d+ \\w{2} \\w{3} (\\w{3}) (\\d+)` (\\d+)",
                            3
                    );

            /* In this variable we will try create datetime-string like "12:00 MAR 18 2014" */
            String parsedReportDate = "00:00 " + parsedDateParts.get(0) + " " + parsedDateParts.get(1) + " " +
                    parsedDateParts.get(2);

            /* Date parser for strings like "00:00 MAR 18 2014" */
            SimpleDateFormat parserSDF = new SimpleDateFormat("HH:mm MMM dd yyyy", Locale.ENGLISH);
            reportDate = parserSDF.parse(parsedReportDate);

        } catch (Exception e) {
            logAnError("Can't parse date from telnet output.", e);
            return result;
        }


        /* Parsing report records from telnet output */

        try {
            String reportLineWithDataPattern = "`\\s?\\d+:\\d+-\\s?(\\d+):\\d+\\s?`\\s?(\\d+)`" +
                    "\\s?(\\d+)`\\s?\\d+:\\d+`\\s?[\\d\\.]+`\\s?(\\d+)`\\s?(\\d+)`";

            List<String> telnetOutputRows = getAllMatches(telnetOutputSb.toString(), reportLineWithDataPattern);

            List<Integer> rawTime = new ArrayList<>();
            List<CheckResult> preparedResult = new ArrayList<>();


            for (String outputRow : telnetOutputRows) {
                List<String> groups = parseTelnetOutputByRegexp(
                        new StringBuilder(outputRow),
                        reportLineWithDataPattern,
                        5
                );

                if (groups.size()<5) continue;

                rawTime.add(Integer.parseInt(groups.get(0)));
            }

            List<Long> dateList = getDateList(rawTime, reportDate.getTime());

            for (int i=0;i<telnetOutputRows.size();i++){
                final List<String> groups = parseTelnetOutputByRegexp(
                        new StringBuilder(telnetOutputRows.get(i)),
                        reportLineWithDataPattern,
                        5
                );

                final List<CheckResult> rawResults = Arrays.asList(
                        new CheckResult()
                                .setEntityId(11)
                                .setValue(Integer.parseInt(groups.get(1))),
                        new CheckResult()
                                .setEntityId(12)
                                .setValue(Integer.parseInt(groups.get(2))),
                        new CheckResult()
                                .setEntityId(13)
                                .setValue(Integer.parseInt(groups.get(3))),
                        new CheckResult()
                                .setEntityId(14)
                                .setValue(Integer.parseInt(groups.get(4)))
                );

                final Date resultRowDate = new Date(dateList.get(i));

                for (CheckResult checkResult : rawResults) {
                    checkResult.setDate(resultRowDate);
                    checkResult.setAttributes(getCheckConfig().getAttributes());
                }

                preparedResult.addAll( rawResults);
            }

            result.addAll(preparedResult);

        } catch (Exception e) {
            logAnError("Can't parse report summary data from telnet output.", e);
            return result;
        }

        return result;
    }

}
