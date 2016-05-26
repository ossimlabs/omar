<!doctype html>
<html ng-app="wmtsApp">
    <head>
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>O2 | WMTS Viewer</title>

      <asset:stylesheet src="wmts-app.manifest.css"/>

    </head>
    <body>

      <div class="container">

        <div class="row">
          <h1>O2 | WMTS Viewer<h1/>
        </div>

        <div class="row">
          <div class="col-md-12" ng-controller="WmtsMapController as map">
            <div id="map" class="map"></div>
          </div>
        </div>

      </div>

      <asset:deferredScripts/>
      <asset:javascript src="wmts-app.manifest.js"/>

    </body>
</html>
