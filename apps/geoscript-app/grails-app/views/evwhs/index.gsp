<html>
  <head>
    <asset:stylesheet src='mapView.css'/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ol3/3.7.0/ol.css" type="text/css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.css" type="text/css">
  </head>
  <body>
    <div class="container-fluid">
      <br>
      <div class="row">
        <div class="col-md-6">
          <form role="search" id="zoomToForm">
            <div class="form-group">
              <div class="input-group" id="zoom-input-group">
                <input class="form-control" id="coordInput" style="box-shadow: 0px 5px 5px #808080; z-index: 9999" type="text" placeholder="Search by coordinates">
                <div class="input-group-btn">
                  <button id="zoomButton" class="btn btn-primary" style="box-shadow: 0px 6px 5px #808080;" type="button">Search</button>
                </div>
              </div>
            </div>
          </form>
        </div>
        <div class="col-md-1">
          <p>Adjust Opacity:</p>
        </div>
        <div class="col-md-4">
          <input class="opacity" type="range" min="0" max="1" step="0.01"/>
        </div>
      </div>
    </div>
    <div id='map'></div>
    <asset:javascript src='mapView.js'/>

    <asset:script>
      $(document).ready(function(){
        var params = ${raw(mapViewParams.encodeAsJSON() as String)};
        MapView.init(params);
      });
    </asset:script>
    <asset:deferredScripts/>
  </body>
</html>
