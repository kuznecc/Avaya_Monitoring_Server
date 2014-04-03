package org.bober.avaya_monitoring.model.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Object for row of table AdmAv.servers
 */

/**
 * This class describe an server with his specified parameters.
 * This instance is a one row of table servers.
 */
public class Server extends AbstractMonitoredEntity {

    public static final List<String> OS_TYPES = new ArrayList<>(Arrays.asList("windows","linux"));

    /* values from table */
    private String ip;
    private String snmpCommunity;
    private String osType = OS_TYPES.get(0);

    public Server() {    }

    public Server(String name, String ip) {
        super( name );
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        if (OS_TYPES.contains(osType)) this.osType = osType;
    }

    public String getSnmpCommunity() {
        return snmpCommunity;
    }

    public void setSnmpCommunity(String snmpCommunity) {
        this.snmpCommunity = snmpCommunity;
    }

    @Override
    public String getPrepareName() {
        return String.format("%s(%s)",getName(),getIp());
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", ip='" + ip + "'" +
                ", osType='" + osType + "'" +
                ", deleted=" + isDeleted() +
                ", description='" + getDescription() + "'" +
                ", snmpCommunity='" + snmpCommunity + "'" +
                '}';
    }
}
