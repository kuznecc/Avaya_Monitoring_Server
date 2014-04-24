package org.bober.avaya_monitoring.service.tasks.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allow execute cmd-command and receive output
 *
 * Usage.
 * 1. Make instance.
 * 2. execute an cmd-command - .execute(command)
 * 3. get output and errorOutput
 *
 * method .execute() was synchronised, so you can make concurrent requests to it.
 */

public class CmdExecutor {

    private static final Logger logger = LoggerFactory.getLogger("avayaMonTask");
    private void logAnError(String message, Exception e) {
        final String errorSrting = String.format("%s Error - %s - %s",
                this.getClass().getSimpleName(),
                message,
                e.getClass().getName()
        );
        logger.error(errorSrting);
        System.err.println(errorSrting);
    }

    private int exitCodeValue;
    private List<String> stdOutputStream = new ArrayList<>();
    private List<String> errOutputStream = new ArrayList<>();

    private String command;
    public String getCommand() {
        return command;
    }
    public CmdExecutor setCommand(String command) {
        this.command = command;
        return this;
    }

    public CmdExecutor() {}

    public synchronized void execute(String cmd){
        command = cmd;
        execute();
    }

    public synchronized void execute(){
        /* clear all old output */
        stdOutputStream.clear();
        errOutputStream.clear();
        /* execute command */
        run();
        try{
            Thread.sleep(500);
        } catch (Exception ignored){}
    }

    private void run() {
        Runtime rt = Runtime.getRuntime();

        try {
            Process proc = rt.exec(command);

            BufferedReader bufferedErrorStdOut = new BufferedReader(
                    new InputStreamReader(
                            proc.getErrorStream()
                    ));

            BufferedReader bufferedStdOut = new BufferedReader(
                    new InputStreamReader(
                            proc.getInputStream()
                    ));

            exitCodeValue = proc.waitFor();

            String line;

            while ((line = bufferedErrorStdOut.readLine()) != null) {
                errOutputStream.add(line);
            }

            while ((line = bufferedStdOut.readLine()) != null) {
                stdOutputStream.add(line);
            }

        } catch (Exception e) {
            logAnError("Can't execute command '"+command+"'",e);
        }
    }



    public int getExitCode() {
        return exitCodeValue;
    }

    public List<String> getStdOutput() {
        return stdOutputStream;
    }

    public List<String> getErrOutput() {
        return errOutputStream;
    }



    /**
     * Method for testing
     */
    public static void main(String[] args) {

        CmdExecutor cmd = new CmdExecutor().setCommand("ping -c 1 localhost");

        System.out.println( "Command = " + cmd.getCommand());
        System.out.println( "Exit code = " + cmd.getExitCode() );


        if ( cmd.getErrOutput().size() > 0 ){

            System.out.println( " errors :" + cmd.getErrOutput().size());

            for (String s : cmd.getErrOutput()) {
                System.out.println( " - " + s );
            }
        }


        if ( cmd.getStdOutput().size() > 0 ){

            System.out.println( " result :" + cmd.getStdOutput().size());

            for (String s : cmd.getStdOutput()) {
                System.out.println( " - " + s );
            }
        }



    }

}
