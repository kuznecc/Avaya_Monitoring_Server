<%@ page import="java.util.List" %>
<%@ page import="static org.bober.avaya_monitoring.model.helper.DateHelper.getCurrentDateInSqlFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // create String variable with options for <select>
    List<String> daoTableList = (List<String>) request.getAttribute("daoTableList");
    StringBuffer sb = new StringBuffer();
    if (daoTableList != null)
        for (String s : daoTableList) {
            sb.append("<option>");
            sb.append(s);
            sb.append("</option>");
        }
    String tablesSelectOptionsList = sb.toString();

%>


<table border="1">
    <tr>
        <td>
            Chose table :
            <select name="tablesList" id="tablesList"><%= tablesSelectOptionsList %>
            </select>
        </td>
        <td>
            <button id="chTableForPeriodBtn">Show</button>
        </td>

    </tr>
</table>

<div id="configurationPageLoadedContent">
    Choose configuration table
</div>

<script type="text/javascript">

    $('#chTableForPeriodBtn').click(function () {
        var url = "${pageContext.request.contextPath}/configuration/showTable/" +
                $("#tablesList option:selected").text();

        loadPage('configurationPageLoadedContent', url);

    });

</script>