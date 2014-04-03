<%--
  This jsp show monitoring service status & controls
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<table border="1">
    <tr>
        <td>
            Send Task :
            <button onclick="pushTask('pushTask/ping');">Ping check</button>
            <button onclick="pushTask('pushTask/cpu');">CPU Load</button>
            <button onclick="pushTask('pushTask/mem');">MEM Load</button>
            <button onclick="pushTask('pushTask/vpUsage');">VP usage</button>
            <br>
            <button onclick="pushTask('pushTask/bcmsVdn');">BCMS vdn</button>
            <button onclick="pushTask('pushTask/bcmsVdn24');">vdn24</button>
            <button onclick="pushTask('pushTask/bcmsTrunk');">BCMS trunk</button>
            <button onclick="pushTask('pushTask/bcmsTrunk24');">trunk24</button>
            <button onclick="pushTask('pushTask/acmLicUtil');">AcmLicUtil</button>
        </td>
    </tr>
    <tr>
        <td>
            Scheduler :
            <button onclick="pushTask('status');">Status</button>
            <button onclick="pushTask('tasksStatus');">Scheduled Tasks Info</button>
            <button onclick="pushTask('pushAllTasks');">Start All Tasks</button>
            <button onclick="pushTask('cancelAllTasks');">Cancel All Tasks</button>
            <br>-------experimental-----
            <button onclick="pushTask('shutdown');">shutdown</button>
            <button onclick="pushTask('purge');">purge</button>
        </td>
    </tr>

</table>
<br>


<table border="1">
    <tr>
        <td>
            <div id="serviceResponse"> service response must be here</div>
        </td>
    </tr>
</table>

<script type="text/javascript">
    function pushTask(task) {
        var url = "${pageContext.request.contextPath}/service/" + task;
        $('#serviceResponse').load(url);
    }
</script>