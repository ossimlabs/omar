<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 12/10/15
  Time: 1:42 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <asset:stylesheet src="imageSpace.css"/>
</head>

<body>
<div id="map"></div>
<asset:javascript src="imageSpace.js"/>
<asset:script>
    $(document).ready(function() {
        var initParams = ${raw( initParams?.encodeAsJSON()?.toString() )};
        ImageSpace.init(initParams);
});
</asset:script>
<asset:deferredScripts/>
</body>
</html>