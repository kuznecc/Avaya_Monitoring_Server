<%@ page import="java.util.List" %>
<%@ page import="org.bober.avaya_monitoring.web.helper.EntityToHtmlTableHelper" %>
<%--
  This jsp receive List<AbstractEntity> and show it like html table
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    EntityToHtmlTableHelper tableHelper = new EntityToHtmlTableHelper();
    List entityList = (List) request.getAttribute("entityList");
%>

${comment}
<%= (entityList.size() > 0)
        ? "<b>Table - " + entityList.get(0).toString().split("\\{")[0] + "</b>"
        : "<b>empty table</b>" %>

<table border=1>
    <%= (entityList.size() > 0)
            ? tableHelper.getHtmlTableRowWithColumnHeaders(entityList.get(0).toString())
            : ""%>

    <% if (entityList.size() > 0) {
        for (Object entity : entityList) {
            out.println( tableHelper.getHtmlTableRow( entity.toString() ) );
        }
       }
    %>
</table>