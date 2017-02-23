<html>
  <head>
    <meta name="layout" content="main">
    <asset:stylesheet src='mapView.css'/>
  </head>
  <body>
    <div class='content'>
      <h1>Map Viewer</h1>
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
