package org.bober.avaya_monitoring.web.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class consist constants and methods for Configuration Page of project
 */
public class UrlHelper {
    final static public String
            URL_VAR_TABLE_NAME = "tableName",
            URL_VAR_ENTITY_ID = "entityId",
            URL_VAR_ENTITY_TYPE = "entityType",

            URL_CONFIG_PAGE_ROOT = "/configuration",
            URL_CONFIG_PAGE_GET_VIEW = "/getView",
            URL_CONFIG_PAGE_GET_TABLE = "/getTable/{"+ URL_VAR_TABLE_NAME +"}",
            URL_CONFIG_PAGE_GET_VIEW_ROW = "/getViewRow/{"+ URL_VAR_TABLE_NAME +"}/{"+ URL_VAR_ENTITY_ID +"}",
            URL_CONFIG_PAGE_GET_EDIT_ROW = "/getEditRow/{"+ URL_VAR_TABLE_NAME +"}/{"+ URL_VAR_ENTITY_ID +"}",
            URL_CONFIG_PAGE_ADD_NEW_ROW = "/getAddNewRow{"+ URL_VAR_ENTITY_TYPE +"}/{"+ URL_VAR_TABLE_NAME +"}",
            URL_CONFIG_PAGE_UPDATE_CHECK_CONFIG = "/updateEntityCheckConfig/{"+ URL_VAR_TABLE_NAME +"}/{"+ URL_VAR_ENTITY_ID +"}",
            URL_CONFIG_PAGE_UPDATE_SERVER = "/updateEntityServer/{"+ URL_VAR_TABLE_NAME +"}/{"+ URL_VAR_ENTITY_ID +"}",
            URL_CONFIG_PAGE_UPDATE_AVAYA_PARAMETER = "/updateEntityAvayaParameter/{"+ URL_VAR_TABLE_NAME +"}/{"+ URL_VAR_ENTITY_ID +"}",
            URL_CONFIG_PAGE_CREATE_SERVER = "/createEntityServer/{"+ URL_VAR_TABLE_NAME +"}",
            URL_CONFIG_PAGE_CREATE_AVAYA_PARAMETER = "/createEntityAvayaParameter/{"+ URL_VAR_TABLE_NAME +"}",
            URL_CONFIG_PAGE_CREATE_CHECK_CONFIG = "/createEntityCheckConfig/{"+ URL_VAR_TABLE_NAME +"}",

            URL_PROPERTY_MON_ENTITY_ID = "monEntityId",
            URL_PROPERTY_ATTRIBUTES = "attributes",
            URL_PROPERTY_FREQUENCY = "frequency",
            URL_PROPERTY_DISABLED = "disabled",
            URL_PROPERTY_DESCRIPTION = "description",
            URL_PROPERTY_NAME = "name",
            URL_PROPERTY_DELETED = "deleted",
            URL_PROPERTY_SERVER_IP = "srvIp",
            URL_PROPERTY_SERVER_SNMP_COMMUNITY = "srvSnmpCommunity",
            URL_PROPERTY_SERVER_OS_TYPE = "srvOsType",
            URL_PROPERTY_SERVER_ID = "serverId",
            URL_PROPERTY_SUBSYSTEM = "subsystem",

            CONFIGURATION_PAGE_DIV_LOADED_CONTENT_ID = "configurationPageLoadedContent";

    /**
     * This method replace variety parts of url
     *      all '{someId}' to '%s'
     * @param url url pattern
     * @return modified url for use it in String.format
     */
    public static String getUrlPattern(String url){
        Pattern pattern  = Pattern.compile("\\{\\w+\\}");
        Matcher matcher = pattern.matcher(url);
        return matcher.replaceAll("%s");
    }


    public static void main(String[] args) {
        System.out.println( getUrlPattern(URL_CONFIG_PAGE_ADD_NEW_ROW));
    }
}
