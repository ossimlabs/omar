<html>
<head>
    <meta name="layout" content="main"/>
    <asset:stylesheet src="mapView.css"/>
</head>

<body>
<div class="content">
    <h1>Map View</h1>

    <div id="map"></div>
</div>
<asset:javascript src="mapView.js"/>
<asset:script>
    $(document).ready(function() {
        var mapViewParams = ${raw( mapViewParams.encodeAsJSON() as String )};
        MapView.init(mapViewParams);
} );
</asset:script>
<asset:deferredScripts/>
</body>
</html>