<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 11/30/15
  Time: 12:16 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>ImageSpace</title>
    <asset:stylesheet src="imageSpace.css"/>
</head>

<body>
<div id="map"></div>
<asset:javascript src="imageSpace.js"/>
<asset:script>
    $(document).ready(function() {
        var imageModel = ${raw( imageModel.encodeAsJSON()?.toString() )};
        ImageSpace.initialize(imageModel);
    });
</asset:script>
<asset:deferredScripts/>
</body>
</html>