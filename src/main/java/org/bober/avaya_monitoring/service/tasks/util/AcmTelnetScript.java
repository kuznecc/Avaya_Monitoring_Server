package org.bober.avaya_monitoring.service.tasks.util;

import org.bober.avaya_monitoring.model.entity.Server;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is a container for script, that they can be executed in the telnet session.
 * Method .call() must return List<String> with telnet output.
 */
public abstract class AcmTelnetScript implements Callable<List<String>> {


    protected AcmTelnetConnection connection = null;
    public void setConnection(AcmTelnetConnection connection) {
        this.connection = connection;
    }


    /**
     * Telnet command that will be executed in the session
     */
    protected String cmd;
    public String getCmd() {
        return cmd;
    }

    @Deprecated
    protected AcmTelnetScript(Server server) {
//        this.server = server;
    }

    @Deprecated
    protected AcmTelnetScript(Server server, String cmd) {
//        this.server = server;
        this.cmd = cmd;
    }

    protected AcmTelnetScript(AcmTelnetConnection connection, String cmd) {
        this.connection = connection;
        this.cmd = cmd;
    }

    /**
     * Execute command from field.
     */
    public void executeCommand(){
        if (cmd!=null && connection!=null)
            connection.write(cmd);
    }


    @Override
    abstract public List<String> call() throws Exception;
}