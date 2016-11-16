<!-- partial-home.html -->
<div ng-controller="HomeController as home">
  <div class="jumbotron text-center">
    <br>
    <br>
    <div class="col-md-8 col-md-offset-2">
      <h1>{{home.title}}</h1>
    </div>
  </div>
  <div class="container">
    <div class="row">
      <div class="col-md-4" ui-sref="map">
        <div class="text-center well well-home">
          <h2>Map</a></h2>
          <div><span class="fa fa-search fa-3x text-info"></span></div>
          <br>
          <p>Search and discover various types of imagery. Use this as a starting
            point for filtering, sorting, and assembling imagery into a collection</p>
          <a type="button" class="btn btn-success" ui-sref="map">View</a>
        </div>
      </div>
      <div ng-show="{{home.tlvAppEnabled}}" class="col-md-4" ng-click="home.go(home.tlvAppLink);">
        <div class="text-center well well-home" ng-href="{{home.tlvAppLink}}" target="_blank">
          <h2>Time Lapse Viewer</h2>
          <div><span class="fa fa-history fa-3x text-info"></span></div>
          <br>
          <p>An on-demand imagery flipbook</p>
          <br><br>
          <a type="button" class="btn btn-success" target="_blank">View</a>
        </div>
      </div>
      <div ng-init="max=10" ng-show="{{home.kmlAppEnabled}}" class="col-md-4">
        <div class="text-center well">
          <h2>KML</h2>
          <div><span class="fa fa-map fa-3x text-info"></span></div>
          <br>
          <p>Download a KML of the last &nbsp; <input style="width:35px" type="number" ng-model="max"> &nbsp; images acquired.</p>
          <br>
          <a type="button" class="btn btn-success" ng-href="{{home.kmlAppLink}}?max={{max}}" target="_blank">Download</a>
        </div>
      </div>
    </div>
    <div class="row">
      <div ng-show="{{home.piwikAppEnabled}}" class="col-md-6" ng-click="home.go(home.piwikAppLink);">
        <div class="text-center well well-home" ng-href="{{home.piwikAppLink}}" target="_blank">
          <h2>PIWIK</h2>
          <div><span class="fa fa-bar-chart fa-3x text-info"></span></div>
          <br>
          <p>View O2 web analytics. Track Key Performance Indicators such as visits, goal conversions
            rates, downloads, keywords and more</p>
          <br>
          <a type="button" class="btn btn-success" ng-href="{{home.piwikAppLink}}" target="_blank">View</a>
        </div>
      </div>
    </div>
  </div>
</div>
