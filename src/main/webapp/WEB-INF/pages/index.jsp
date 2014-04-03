
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

</head>
<body>

<%@include file="header.jsp" %>

<h1>${message}</h1>


<button id="statisticsPageBtn">* Statistics page *</button>
<button id="serviceControlsBtn">* Service controls *</button>

<div id="indexPageLoadedContent"> - </div>



<script type="text/javascript">

    $('#statisticsPageBtn').click(function () {
        var url = "${pageContext.request.contextPath}/statistics/getView";
        loadPage('indexPageLoadedContent', url);
    });

    $('#serviceControlsBtn').click(function () {
        var url = "${pageContext.request.contextPath}/service/getView";
        loadPage('indexPageLoadedContent', url);
    });
</script>



<%@include file="footer.html" %>
</body>
</html>