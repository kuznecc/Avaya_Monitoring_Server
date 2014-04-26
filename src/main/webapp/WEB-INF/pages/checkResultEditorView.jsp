<%@ page import="java.util.List" %>
<%@ page import="org.bober.avaya_monitoring.web.helper.EntityToHtmlTableHelper" %>
<%@ page import="org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity" %>
<%@ page import="org.bober.avaya_monitoring.model.entity.CheckConfig" %>
<%@ page import="java.util.Collections" %>
<%--
  This jsp receive List<AbstractEntity> and show it like html table
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
//    EntityToHtmlTableHelper tableHelper = new EntityToHtmlTableHelper();



//    new
    // create String variable with options for <drop down monitored entities list>
//    List<String> daoTableList = (List<String>) request.getAttribute("daoTableList");
//    StringBuffer sb = new StringBuffer();
//    if (daoTableList != null)
//        for (String s : daoTableList) {
//            sb.append("<option>");
//            sb.append(s);
//            sb.append("</option>");
//        }
//    String tablesSelectOptionsList = sb.toString();

    String tableName = (String) request.getAttribute("tableName");

    List<CheckConfig> entityList =
            (List<CheckConfig>) request.getAttribute("checkConfigList");

    List<AbstractMonitoredEntity> monitoredEntityList =
            (List<AbstractMonitoredEntity>) request.getAttribute("monitoredEntityList");


%>

<%--${tableName}--%>

<%= (entityList.size() > 0)
        ? "<b>Table - " + tableName + "</b>"
        : "<b>empty table "+ tableName +"</b>"
%>
<br>
<br>

<%
    out.print("entity list = <br>");
    for (CheckConfig checkConfig : entityList) {
        out.print("___"+checkConfig+"<br>");
    }
%>
<br>
<br>
<%
    out.print("list of all monitored entity = <br>");
    for (AbstractMonitoredEntity monEn : monitoredEntityList) {
        out.print("___" + monEn + "<br>");
    }
%>
<br>

<%--<table border=1>--%>
    <%--<%= (entityList.size() > 0)--%>
            <%--? tableHelper.getHtmlTableRowWithColumnHeaders(entityList.get(0).toString())--%>
            <%--: ""--%>
    <%--%>--%>

    <%--<% if (entityList.size() > 0) {--%>
        <%--for (Object entity : entityList) {--%>
            <%--out.println( tableHelper.getHtmlTableRow( entity.toString() ) );--%>
        <%--}--%>
       <%--}--%>
    <%--%>--%>
<%--</table>--%>