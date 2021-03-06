package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.entity.Server;
import org.bober.avaya_monitoring.service.tasks.util.CmdExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This monitoring task exeute commands like
 * This monitoring task send ping packages to server and return
 * linux   : ping -c 1 10.7.2.250
 * windows : hrping -n 1 1.2.7.250
 *
 * parse output, calculate and return AVG latency in microseconds (us)
 *
 * On the windows application server must be pre installed "CFOS hrping"
 */
public class PingTask extends AbstractTask{

    // TODO: in future this class need to be refactored. Extract superclass like AbstractSnmpTask

    {
        setMeasurementUnit("us"); // microseconds
    }

    @Override
    public String getDescription(){
        return String.format(
                "Этот таск определяет среднее время прохождения ping-пакета к проверяемому серверу." +
                        "Проверка осуществляется за счет парсинга вывода программы ping(linux)/hrping(win)," +
                        "которая выполняется с атрибутом -c(lin)/-n(win), определяющим количество отправляемых " +
                        "пакетов(%s)." +
                        "При парсинге сперва проверяется наличие потерянных пакетов (lost/timeout), после чего" +
                        "парсится результат (avg). Результат возвращается в микросекундах(us)." +
                        "Если хоть один пакет был потерян или не удалось запарсить результат, то таск " +
                        "возвращает null.",
                countOfPingPackets
        );
    }

    /**
     *  This property set up how many ping package need to send during one task iteration
     */
    private int countOfPingPackets = 3;
    public void setCountOfPingPackets(int countOfPingPackets) {
        this.countOfPingPackets = countOfPingPackets;
    }

    /* If server OS is windows then true */
    private boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");



    /**
     * This method prepare command string, execute it in new instance of CmdExecutor,
     * check output, parse it and return list of CheckResult
     *
     * @return list of CheckResult instances for specified entity's
     */
    @Override
    protected List<CheckResult> childTaskRunLogic() {
        /*                  -==== Unix Output example ====- :
         * ==> ping -c 1 10.7.2.250
         * PING 10.7.2.250 (10.7.2.250): 56 data bytes
         * 64 bytes from 10.7.2.250: icmp_seq=0 ttl=126 time=1.718 ms
         *
         * --- 10.7.2.250 ping statistics ---
         * 1 packets transmitted, 1 packets received, 0.0% packet loss
         * round-trip min/avg/max/stddev = 1.718/1.718/1.718/0.000 ms
         *
         * ==> ping -c 1 1.7.2.250
         * PING 1.7.2.250 (1.7.2.250): 56 data bytes
         *
         * --- 1.7.2.250 ping statistics ---
         * 1 packets transmitted, 0 packets received, 100.0% packet loss
         *
         *
         *                  -==== Windows Output example ====- :
         * ==> hrping -n 1 10.7.2.250
         * This is hrPING v5.06.1143 by cFos Software GmbH -- http://www.cfos.de
         *
         * Source address is 127.0.0.1; using ICMP echo-request, ID=780e
         * Pinging 10.7.2.250 [10.7.2.250]
         * with 32 bytes data (60 bytes IP):
         *
         * From 10.7.2.250: bytes=60 seq=0001 TTL=128 ID=389d time=0.160ms
         *
         * Packets: sent=1, rcvd=1, error=0, lost=0 (0.0% loss) in 0.000160 sec
         * RTTs in ms: min/avg/max/dev: 0.160 / 0.160 / 0.160 / 0.000
         * Bandwidth in kbytes/sec: sent=375.000, rcvd=375.000 
         *
         * ==> hrping -n 1 1.2.7.250
         * This is hrPING v5.06.1143 by cFos Software GmbH -- http://www.cfos.de
         *
         * Source address is 10.7.2.250; using ICMP echo-request, ID=7c13
         * Pinging 1.2.7.250 [1.2.7.250]
         * with 32 bytes data (60 bytes IP):
         *
         * Timeout waiting for seq=0001
         *
         * Packets: sent=1, rcvd=0, error=0, lost=1 (100.0% loss) in 0.000000 sec
         * RTTs in ms: min/avg/max/dev: 0.000 / 0.000 / 0.000 / 0.000
         * Bandwidth in kbytes/sec: sent=0.000, rcvd=0.000 
         */

        /* Prepare command */

        Server server = (Server)getMonitoredEntity();

        String cmd = (isWindows)
                ? "hrping.exe -n "
                : "ping -c ";

        cmd += countOfPingPackets + " " + server.getIp();

        CmdExecutor cmdResult = new CmdExecutor();
        cmdResult.execute(cmd);

        List<String> errOutput = cmdResult.getErrOutput();
        List<String> stdOutput = cmdResult.getStdOutput();


        if (errOutput!=null && errOutput.size() > 0) {
            logAnError(
                    String.format("ERR output:%s", errOutput)
            );
        }

        if (stdOutput==null || stdOutput.size() < 1 ||
                errOutput == null || errOutput.size() > 0) {
            return new ArrayList<>();
        }

        return (isSomePacketsLost(cmdResult))
                ? getTaskResultList(getResult(cmdResult))
                : new ArrayList<CheckResult>();
    }

    /**
     * return false if during execution command we lost anyone packet or can't find ping-statistic
     * string in the CmdExecutor output
     */
    private boolean isSomePacketsLost(CmdExecutor cmdResult){
        Pattern statisticStringPattern = Pattern.compile( (isWindows)
                ?" lost=\\d+ \\(([\\d.]+)% loss\\) in "
                :"\\d+ packets transmitted, \\d+ packets received, (\\d+.\\d+)% packet loss"
        );

        Float lostPackagesPercent = null;
        for (String line : cmdResult.getStdOutput()) {
            Matcher matcher = statisticStringPattern.matcher(line);
            if (matcher.find()){
                lostPackagesPercent = Float.parseFloat(matcher.group(1));
                break;
            }
        }

        if (lostPackagesPercent == null) {
            logger.error("can't parse lost packages percent from output");
        }

        return (lostPackagesPercent!=null && lostPackagesPercent<=0);
    }

    /**
     * return avg ping latency
     */
    private Integer getResult(CmdExecutor cmdResult){
        Pattern resultStringPattern = Pattern.compile(
                (isWindows)
                        ? "min/avg/max/dev: \\d+.\\d+ / \\d+.\\d+ / (\\d+.\\d+) / \\d+.\\d+"
                        : "round-trip min/avg/max/stddev = \\d+.\\d+/\\d+.\\d+/(\\d+.\\d+)/\\d+.\\d+ ms"
        );

        Float avgLatency = null;
        for (String line : cmdResult.getStdOutput()) {
            Matcher matcher = resultStringPattern.matcher(line);
            if (matcher.find()){
                avgLatency = Float.parseFloat(matcher.group(1));
                break;
            }
        }

        if (avgLatency == null) {
            logger.error("can't parse avg latency from output");
        }

        return (avgLatency != null)
                        ? Math.round(avgLatency * 1000)
                        : null ;
    }

}
