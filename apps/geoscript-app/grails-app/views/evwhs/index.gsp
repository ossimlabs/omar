<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>DG Imagery</title>

    <asset:stylesheet src='mapView.css'/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ol3/3.7.0/ol.css" type="text/css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.css" type="text/css">

  </head>
  <body>
    <div class="container-fluid">
      <!-- <div class="row text-center">
        <h1>Digitalglobe Imagery</h1>
      </div> -->
    </div>
    <div id='map'>
      <div class="col-md-4">
        <form role="search" id="zoomToForm" class="searchForm">
          <div class="form-group">
            <div class="input-group" id="zoom-input-group">
              <input class="form-control" id="coordInput" style="box-shadow: 0px 5px 5px #808080; z-index: 9999" type="text" placeholder="Search by coordinates">
              <div class="input-group-btn">
                <button id="zoomButton" class="btn btn-primary searchForm-button" type="button">Search</button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div class="map-display-element dgi-opacitySlider">
      <small>Imagery Opacity</small>
      <input type="range" class="opacity" min="0" max="1" step="0.01" />
    </div>

    <div class="map-display-element map-zoomLevel">
      <small>Zoom Level: <span id="zoomLevel"></span></small><br>
      <small><em>Imagery not visible above level 11</em></small>
    </div>

    <div class="map-display-element dgi-label">
      <small id="mouseCoords" class="map-cord-div" tooltip-placement="top"></small>
    </div>

    <!-- Right-click context menu -->
    <div class="modal" id="contextMenuDialog" role="dialog" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4>You Clicked Here:</h4>
          </div>
          <div align="center" class="modal-body"></div>
          <div class="modal-footer">
            <button type="button" class="btn btn-warning" data-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>

    <asset:javascript src='mapView.es6'/>

    <asset:script>

      $(document).ready(function(){
        var params = ${raw(mapViewParams.encodeAsJSON() as String)};
        MapView.init(params);
      });

    </asset:script>
    <asset:deferredScripts/>
  </body>
</html>
