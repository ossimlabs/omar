<div ng-controller="MapImageController as image">
  <nav style="margin-top: -15px;" class="navbar navbar-default" role="navigation">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
        data-target="#map-navbar-collapse" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
    </div>
    <div class="collapse navbar-collapse" id="map-navbar-collapse">
      <ul class="nav navbar-nav">
        <!-- <li class="dropdown">
          <a  class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
            aria-expanded="false">Select<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li ng-click=""><a>Rectangle</a></li>
            <li ng-click=""><a>Freehand Polygon</a></li>
            <li ng-click=""><a>Add a point</a></li>
          </ul>
        </li> -->
        <li class="dropdown">
          <a  class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
            aria-expanded="false">Measure<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li ng-click="image.measure(true, 'LineString')"><a>Path</a></li>
            <li ng-click="image.measure(true, 'Polygon')"><a>Area</a></li>
            <li ng-click="image.measureClear()"><a>Clear Measure</a></li>
          </ul>
        </li>
        <li ng-click="image.screenshot()"><a>Screenshot</a></li>
        <li class="dropdown">
          <a  class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
            aria-expanded="false">Zoom<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li ng-click="image.zoomToFullRes()"><a>Full Resolution</a></li>
            <li ng-click="image.zoomToFullExtent()"><a>Maximum Extent</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>
  <div class="container-fluid">
    <div class="row">
      <div class="col-md-3 well">
        <div class="panel panel-info">
          <div class="panel-heading">
            <h3 class="panel-title">Band Selection</h3>
          </div>
          <div class="panel-body">
            <div id="band-type" class="image-bands">
              <ui-select id="bandTypeItem"
                  ng-model="bandTypeItem"
                  on-select="showBands($select.selected.value)"
                  ng-disabled="enableBandType != true"
                  theme="selectize">
                <ui-select-match>
                    <span ng-bind="$select.selected.value"></span>
                </ui-select-match>
                <ui-select-choices repeat="val in bandTypeValues">
                    <span ng-bind="val.value"></span>
                </ui-select-choices>
              </ui-select>
            </div>
            </br>
            <div id="image-space-bands">
              <div id="gray-image-space-bands" class="image-bands image-band-div">
                <label for="male">Band:&nbsp;</label>
                <ui-select id="grayImageItem" theme="selectize" ng-model="grayImageItem" on-select="onBandSelect($select.selected.value, 'gray')">
                  <ui-select-match>
                      <span ng-bind="$select.selected.value"></span>
                  </ui-select-match>
                  <ui-select-choices repeat="val in bandValues">
                      <span ng-bind="val.value"></span>
                  </ui-select-choices>
                </ui-select>
              </div>
              <div id="rgb-image-space-bands" class="image-bands">
                <div id="redImageBand" class="image-band-div">
                  <label>Red:&nbsp;</label>
                  <ui-select id="redImageItem" theme="selectize" ng-model="redImageItem" on-select="onBandSelect($select.selected.value, 'red')">
                    <ui-select-match>{{$select.selected.value}}</ui-select-match>
                    <ui-select-choices repeat="val.key as val in bandValues | filter: $select.search">
                        <span ng-bind-html="val.value | highlight: $select.search"></span>
                    </ui-select-choices>
                  </ui-select>
                </div>
                <div id="greenImageBand" class="image-band-div">
                  <label>Green:&nbsp;</label>
                  <ui-select id="greenImageItem" theme="selectize" ng-model="greenImageItem" on-select="onBandSelect($select.selected.value, 'green')">
                    <ui-select-match>{{$select.selected.value}}</ui-select-match>
                    <ui-select-choices repeat="val.key as val in bandValues | filter: $select.search">
                        <span ng-bind-html="val.value | highlight: $select.search"></span>
                    </ui-select-choices>
                  </ui-select>
                </div>
                <div id="blueImageBand" class="image-band-div">
                  <label>Blue:&nbsp;</label>
                  <ui-select id="blueImageItem" theme="selectize" ng-model="blueImageItem" on-select="onBandSelect($select.selected.value, 'blue')">
                    <ui-select-match>{{$select.selected.value}}</ui-select-match>
                    <ui-select-choices repeat="val.key as val in bandValues | filter: $select.search">
                        <span ng-bind-html="val.value | highlight: $select.search"></span>
                    </ui-select-choices>
                  </ui-select>
                </div>
              </div>
            </br>
            </div>
          </div>
        </div>
        <div class="panel panel-info">
          <div class="panel-heading">
            <h3 class="panel-title">Dynamic Range Adjustment</h3>
          </div>
          <div class="panel-body">
            <ui-select
              ng-model="draType"
              on-select="onDraSelect($select.selected.value)"
              theme="selectize">
              <ui-select-match>
                <span ng-bind="$select.selected.name"></span>
              </ui-select-match>
              <ui-select-choices repeat="val in draTypes">
                <span ng-bind="val.name"></span>
              </ui-select-choices>
            </ui-select>
          </div>
        </div>
        <div class="panel panel-info">
          <div class="panel-heading">
            <h3 class="panel-title">Measurement</h3>
          </div>
          <div class="panel-body">
            <div class="text-center">
              <small class="text text-success">{{image.measureMessage}}</small>
              <br>
              <h4>Measure Type:&nbsp;&nbsp;<span class="label label-info">{{image.measureType}}</span></h4>
            </div>
            <div>
              <ui-select
              ng-model="selected.value"
              theme="selectize"
              on-select="image.setMeasureUnits($select.selected.value)">
                <ui-select-match>
                    <span ng-bind="$select.selected.name"></span>
                </ui-select-match>
                <ui-select-choices repeat="item in (itemArray | filter: $select.search) track by item.id">
                    <span ng-bind="item.name"></span>
                </ui-select-choices>
            </ui-select>
            </div>
            <br>
            <ul style="padding-left: 0px">
              <li class="list-group-item">Geodetic Dist.<span class="badge">{{image.geodDist}}</span></li>
              <li class="list-group-item">Rectilinear Dist.<span class="badge">{{image.recDist}}</span></li>
              <li class="list-group-item" ng-show="image.displayAzimuth">Azimuth Bearing<span class="badge">{{image.azimuth}}</span></li>
              <li class="list-group-item" ng-show="image.measurePolygon">Area<span class="badge">{{image.area}}</span></li>
            </ul>
            <div class=" text-center">
              <small class="text text-warning">Not certified for targeting</small>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-9">
        <div id="imageMap" class="map imageMap imageMapBorder">
          <div class="imageLinkBtns imageShareButton">
            <a ng-href="" target="_blank" ng-click="">
              <i class="fa fa-share-alt fa-border text-primary"
              tooltip-placement="left-bottom"
              uib-tooltip="Share a link to this image"></i>
            </a>&nbsp;&nbsp;
          </div>
          <div class="imageLinkBtns imageShareButton">
            <a ng-href="" target="_blank" ng-click="image.shareModal(image.imageMapPath)">
              <i class="fa fa-share-alt fa-border text-primary"
              tooltip-placement="left-bottom"
              uib-tooltip="Share a link to this image"></i>
            </a>&nbsp;&nbsp;
          </div>
          <div class="imageLinkBtns mapDownloadButton">
            <a ng-href="" target="_blank" ng-click="image.archiveDownload(image.imageId)">
              <i class="fa fa-download fa-border text-primary" tooltip-placement="left-bottom"
                style="cursor: pointer;" uib-tooltip="Download and archive files"></i>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
