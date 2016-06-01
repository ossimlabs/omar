<%@ page contentType="text/html;charset=UTF-8" %>
<html ng-app="wmtsApp">
    <head>
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>O2 | WMTS Viewer</title>

      <asset:stylesheet src="wmts-app.manifest.css"/>
    </head>
    <body ng-controller="WmtsMapController as map">

      <div class="container-fluid" >

        <nav class="navbar navbar-default">
          <div class="container-fluid">
            <div class="navbar-header">
              <a class="navbar-brand">O2 | WMTS Viewer</a>
            </div>
          </div>
        </nav>

      </div>
      <div id="map" class="map"></div>

      <asset:script>

          var AppWmts = (function () {

              var APP_CONFIG = ${raw( clientConfig.encodeAsJSON() as String )};

              return {

                  APP_CONFIG: APP_CONFIG

              }

          })();

      </asset:script>
      <asset:deferredScripts/>
      <asset:javascript src="wmts-app.manifest.js"/>

    </body>
</html>
