package org.bober.avaya_monitoring.model.entity;

/**
 * This class describe an parameter of Avaya appliance.
 * (for example ACM agent license count)
 * This instance is a one row of table avaya_parameter.
 */
public class AvayaParameter extends AbstractMonitoredEntity {

    String subSystem;
    int serverId;

    Server server;

    public AvayaParameter(){ }

    public AvayaParameter(String name, int serverId) {
        super(name);
        this.serverId = serverId;
    }

    public String getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String getPrepareName() {
        return String.format("%s(%s)",getName(),getSubSystem());
    }

    @Override
    public String toString() {
        return "AvayaParameter{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", subSystem='" + subSystem + "'" +
                ", serverId=" + serverId +
                ", deleted=" + isDeleted() +
                ", description='" + getDescription() + "'" +
                '}';
    }
}
