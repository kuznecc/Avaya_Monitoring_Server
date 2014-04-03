package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.service.tasks.util.AcmTelnetConnection;
import org.bober.avaya_monitoring.service.tasks.util.AcmTelnetScript;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This monitoring task make telnet-connection to the ACM server (getMonitoredEntityServer()),
 * execute command 'display system-parameters customer-options', parse result
 * and return list of CheckResult instances with licences data.
 * <p/>
 * EntityId  -  Entity description from avaya_parameters table
 * id_29 - AvayaParameter.Licenses
 * id_15 - AvayaParameter.LicPlatformPortsUsed
 * id_16 - AvayaParameter.LicStationsUsed
 * id_17 - AvayaParameter.LicLoggedInAcdAgentsUsed
 * id_18 - AvayaParameter.LicLoggedInAdvocateAgentsUsed
 * id_21 - AvayaParameter.LicProductAgentScUsed
 * id_22 - AvayaParameter.LicProductIpApiAUsed
 * id_19 - AvayaParameter.LicLoggedInIpSoftphoneAgentsUsed
 * id_20 - AvayaParameter.LicLoggedInSipEasAgentsUsed
 * id_23 - AvayaParameter.LicProductIpApiBUsed
 * id_24 - AvayaParameter.LicProductIpAgentUsed
 * id_25 - AvayaParameter.LicProductIpPhoneUsed
 * id_26 - AvayaParameter.LicProductIpRoMaxUsed
 * id_27 - AvayaParameter.LicProductIpSoftUsed
 * id_28 - AvayaParameter.LicProductOneXCommUsed
 */
public class AcmTelnetLicenseUtilisation extends AbstractAcmTelnetTask {
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

        AcmTelnetScript acmScript =
                new AcmTelnetScript(getAcmTelnetConnection(), "display system-parameters customer-options") {
                    @Override
                    public List<String> call() throws Exception {
                        List<String> result = new ArrayList<>();

                        executeCommand();

                        for (int i = 0; i < 10; i++) {
                            result.add(connection.readUntil(3000));
                            connection.nextPage();
                        }

                        return result;
                    }
                };


        /* Execute acm telnet script */

        List<String> telnetOutput = null;
        try {
            telnetOutput = getAcmTelnetConnection().executeAvayaTelnetCmd(acmScript);
        } catch (Exception e) {
            logAnError("Can't execute prepared AcmTelnetScript over TelnetConnection class", e);
        }

        return getCheckResultsFromTelnetOutput(telnetOutput);
    }

    /**
     * This method parse received telnet-output for specified licenses information
     * and return it like a CheckResult objects list.
     *
     * @param telnetOutput - output from telnet session, which was executed AcmTelnetScript
     * @return list of CheckResult instances for specified entity's. If telnetOutput null or empty
     * then return CheckResult instances for all entity's with null value
     */
    List<CheckResult> getCheckResultsFromTelnetOutput(List<String> telnetOutput) {

        StringBuilder sb = new StringBuilder();
        if (telnetOutput != null) {
            for (String telnetOutputRow : telnetOutput) {
                sb.append(telnetOutputRow);
            }
        }

        List<CheckResult> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Date now = new Date(cal.getTimeInMillis());

//        result.add(getCheckResult(now, 15, "`Platform Maximum Ports: `\\d+`(\\d+) ", sb));
//        result.add(getCheckResult(now, 16, "`Maximum Stations: `\\d+ `(\\d+) ", sb));
        result.add(getResultForId(now, 17, "Logged-In ACD Agents: `\\d+ `(\\d+) `", sb));
        result.add(getResultForId(now, 19, "Logged-In IP Softphone Agents: `\\d+ `(\\d+) `", sb));
        result.add(getResultForId(now, 20, "Logged-In SIP EAS Agents: `\\d+ `(\\d+) `", sb));
        result.add(getResultForId(now, 21, "`AgentSC `: `\\d+ `(\\d+) ", sb));
//        result.add(getCheckResult(now, 22, "`IP_API_A `: `\\d+ `(\\d+) ", sb));
//        result.add(getCheckResult(now, 23, "`IP_API_B `: `\\d+ `(\\d+) ", sb));
        result.add(getResultForId(now, 24, "`IP_Agent `\\d+ `: `\\d+ `(\\d+) `", sb));
        result.add(getResultForId(now, 25, "`IP_Phone `: `\\d+`(\\d+) ", sb));
        result.add(getResultForId(now, 26, "`IP_ROMax `: `\\d+`(\\d+) ", sb));
        result.add(getResultForId(now, 27, "`IP_Soft `\\d+ `: `\\d+ `(\\d+) ", sb));
        result.add(getResultForId(now, 28, "`oneX_Comm `: `\\d+`(\\d+)", sb));
        return result;
    }


    /**
     * Stand alone integration test of this task
     */
    public static void main(String[] args) {

        final AcmTelnetConnection connection = new AcmTelnetConnection() {{
            setTelnetServerIp("10.7.2.33");
            setTelnetServerPort(5023);
            setTelnetLogin("ayudin");
            setTelnetPassword("******");
        }};

        final AcmTelnetLicenseUtilisation acmTelnetLicenseUtilisation = new AcmTelnetLicenseUtilisation() {{
            setAcmTelnetConnection(connection);
        }};

        long startTime = System.nanoTime();

        /* Receive telnet output from ACM server */
        final List<CheckResult> checkResults = acmTelnetLicenseUtilisation.childTaskRunLogic();

        long execTime = System.nanoTime() - startTime;

        System.out.println("execution time : " + execTime + "nanoseconds");

        for (CheckResult checkResult : checkResults) {
            System.out.println(checkResult);
        }
    }
}
