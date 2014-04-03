<%@ page import="java.util.*" %>
<%@ page import="org.bober.avaya_monitoring.model.entity.CheckResult" %>
<%--  this view make convertation of received list to json  --%>
<%-- test link : http://localhost:8080/statistics/getForPeriod/servers_ping?startDate=2014-02-16_00.00.00&endDate=2014-02-20_23.59.00&type=graph --%>

<%
    List<CheckResult> entityList = (List<CheckResult>) request.getAttribute("entityList");


    StringBuffer br = new StringBuffer();

    Map<Integer, List<Integer>> valueMap = new HashMap<Integer, List<Integer>>();
    for (CheckResult checkResult : entityList) {
        int entityId = checkResult.getEntityId();

        if ( !valueMap.containsKey(entityId) ){
            List<Integer> values = new ArrayList<Integer>();
            valueMap.put(entityId, values);
        }

        valueMap.get( entityId ).add(checkResult.getValue());
    }


    br.append("[");
    Set<Integer> servIdSet = valueMap.keySet();
    int i=0;

    for (Integer srvId : servIdSet) {

        br.append("{");
        br.append("\"key\" : \"srv"+srvId+"\",");
        br.append("\"values\" : "+Arrays.toString(valueMap.get(srvId).toArray()) + "," );
        br.append("\"color\" : \"#ff7f0e\"");
        br.append("}");

        if  (i<servIdSet.size()-1) {
            br.append(",");
        }
        i++;
    }
    br.append("]");

    //[{"entityId" : 8,"value" : [40, 40],"color" : "#ff7f0e"}]
    out.print( br.toString() );

%>
