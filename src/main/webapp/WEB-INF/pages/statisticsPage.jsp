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

    // Current date. example 12-02-2014
    String today = getCurrentDateInSqlFormat().split(" ")[0].trim();
%>


<table border="1">
    <tr>
        <td>
            Chose table :
            <select name="tablesList" id="tablesList"><%= tablesSelectOptionsList %>
            </select>
        </td>
        <td>
            start date:
            <input type="text" size="12" value="<%= today %>" id="startDate"/>
            <input type="text" size="8" value="00:00:00" id="startDateTime"/><br>
            end date :
            <input type="text" size="12" value="<%= today %>" id="endDate"/>
            <input type="text" size="8" value="23:59:00" id="endDateTime"/><br>
            <button id="chTableForPeriodBtn">Show</button>
        </td>
        <td>
            days :
            <input type="text" size="3" value="2" id="daysCount"/><br>
            <button id="chTableForDayBtn">Show</button>  <br>

        </td>
    </tr>
</table>

<div id="statisticPageLoadedContent">
    Chose data
</div>

<script type="text/javascript">

    // Enable JsDatePick for the text fields
    new JsDatePick({
        useMode: 2,
        target: "startDate",
        dateFormat: "%Y-%m-%d"
    });
    new JsDatePick({
        useMode: 2,
        target: "endDate",
        dateFormat: "%Y-%m-%d"
    });
    // Set today date to the text fields
    $("#startDate").value = getCurrentDate();
    $("#endDate").value = getCurrentDate();

    // Enable clockpick for the text fields
    $("#startDateTime").clockpick({
        starthour: 0,
        endhour: 23,
        military: true,
        showminutes: true
    });
    $("#endDateTime").clockpick({
        starthour: 0,
        endhour: 23,
        military: true,
        showminutes: true
    });


    $('#chTableForPeriodBtn').click(function () {
        var params = jQuery.param({
            startDate: $('#startDate').val() + "_" + $('#startDateTime').val().replace(/:/g, '.'),
            endDate: $('#endDate').val() + "_" + $('#endDateTime').val().replace(/:/g, '.')
        });
        var url = "${pageContext.request.contextPath}/statistics/getForPeriod/" +
                $("#tablesList option:selected").text() + "?" + params;

        loadPage('statisticPageLoadedContent', url);

    });

    $('#chTableForDayBtn').click(function () {
        var params = jQuery.param({
            days: $('#daysCount').val()
        });
        var url = "${pageContext.request.contextPath}/statistics/getForDays/" +
                $("#tablesList option:selected").text() + "?" + params;

        loadPage('statisticPageLoadedContent', url);

    });

</script>