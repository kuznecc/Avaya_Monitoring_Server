<%@ page import="java.util.List" %>
<%@ page import="org.bober.avaya_monitoring.web.helper.EntityToHtmlTableHelper" %>
<%@ page import="org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity" %>
<%@ page import="org.bober.avaya_monitoring.model.entity.CheckConfig" %>
<%--
  This jsp receive List<CheckConfig> and other and show it like html table
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    EntityToHtmlTableHelper tableHelper = new EntityToHtmlTableHelper();

    /* Load data obtained from controller */

    String tableName = (String) request.getAttribute("tableName");

//    @SuppressWarnings()
    List<CheckConfig> entityList =
            (List<CheckConfig>) request.getAttribute("checkConfigList");

    List<AbstractMonitoredEntity> monitoredEntityList =
            (List<AbstractMonitoredEntity>) request.getAttribute("monitoredEntityList");
%>

<%= (entityList.size() > 0)
        ? "<b>Table - " + tableName + "</b>"
        : "<b>empty table " + tableName + "</b>"
%>

<br>

<table border=1>
    <%= (entityList.size() > 0)
            ? tableHelper.getHtmlTableForCheckConfigList(entityList, monitoredEntityList)
            : ""
    %>
</table>