package org.bober.avaya_monitoring.service.tasks.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 */
public class CmdExecutorTest {
    /* If server OS is windows then true */
    private boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    @Test
    public void testExecute() throws Exception {

        String cmd = "ping";

        CmdExecutor cmdResult = new CmdExecutor();
        cmdResult.execute(cmd);

        assertTrue("execution '" + cmd + "'", cmdResult.getStdOutput().size() == 0);
        assertTrue("execution '" + cmd + "'", cmdResult.getErrOutput().size() > 0);

        cmd = (isWindows)
                ? "ping.exe -n 1 127.0.0.1"
                : "ping -c 1 127.0.0.1";

//        cmdResult = new CmdExecutor();
        cmdResult.execute(cmd);

        assertTrue("execution '" + cmd + "'", cmdResult.getStdOutput().size() > 0);
        assertTrue("execution '" + cmd + "'", cmdResult.getErrOutput().size() == 0);
    }

}
