<html>
    <head>
        <meta name="layout" content="main"/>
        <asset:stylesheet src="mapView.css"/>
        <asset:stylesheet src="ol3-layerswitcher.css"/>
    </head>
    <body>
        <div class="content">
            <h1>Map View</h1>
            <div id="map"></div>
        </div>
        <asset:javascript src="mapView.js"/>
        <asset:javascript src="ol3-layerswitcher.js"/>
        <asset:script>
        $(document).ready(function() {
            MapView.init();
        } );
        </asset:script>
        <asset:deferredScripts/>
    </body>
</html>
