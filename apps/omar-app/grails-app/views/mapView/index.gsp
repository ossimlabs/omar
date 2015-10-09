<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main"/>
    <asset:stylesheet src='mapView.css'/>
</head>

<body>
<div class="nav">
    <ul>
        <li><g:link class="home" uri="/">Home</g:link></li>
    </ul>
</div>

<div class="content">
    <h1>Map View</h1>
    <div id='map'></div>
</div>
<asset:javascript src='mapView.js'/>
<asset:script>
$(document).ready(function(){
	MapView.init();
});
</asset:script>
<asset:deferredScripts/>
</body>
</html>