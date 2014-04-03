<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="org.bober.avaya_monitoring.model.entity.AbstractEntity" %>
<%@ page import="org.bober.avaya_monitoring.model.entity.CheckResult" %>
<%@ page import="org.bober.avaya_monitoring.model.helper.CollectionHelper" %>
<%@ page import="org.bober.avaya_monitoring.web.helper.charts.LineChart" %>
<%--
  Show div with High Chart graphic
--%>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/js/highcharts.js">
</script>

<%
    List<AbstractEntity> entityList = (List<AbstractEntity>) request.getAttribute("entityList");

    List<CheckResult> data = null;
    if (entityList.size() >0 &&
            entityList.get(0) instanceof CheckResult) {
        data = CollectionHelper.castList(CheckResult.class, entityList);
    }

    String tableName = (String) request.getAttribute("tableName");
%>

<br>
<div id="myLineChart" style="max-width: 1000px;min-width: 700px"></div>

<script type="text/javascript">
    chartOptions = <%= LineChart.getChart("myLineChart",tableName, data) %>
    new Highcharts.Chart( chartOptions );
</script>