<div ng-controller="MapImageController as image">
  <nav class="navbar navbar-default imageMapNav" role="navigation">
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
      <div class="col-sm-8">
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
          <li
            ng-click="image.archiveDownload(image.imageId)"
            tooltip-placement="right"
            uib-tooltip="Download the raw image, histogram and overview files"><a>Download</a>
          </li>
          <li class="dropdown">
            <a
              class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false" tooltip-placement="right" uib-tooltip="Measure area and distances, and calculate horizontal and vertical error for points">Measure<span class="caret"></span></a>
            <ul class="dropdown-menu">
              <li class="dropdown-header">Measurements</li>
              <li
                ng-click="image.measure(true, 'LineString')" tooltip-placement="right"
                uib-tooltip="Draw lines, and calculate their distance"><a>Path</a></li>
              <li
                ng-click="image.measure(true, 'Polygon')" tooltip-placement="right"
                uib-tooltip="Create a polygon, and calculate the area within"><a>Area</a></li>
              <li
                ng-click="image.measureClear()" tooltip-placement="right"
                uib-tooltip="Clear the measurement and close the panel"><a>Clear</a></li>
              <li role="separator" class="divider"></li>
              <li class="dropdown-header">Position Quality Evaluator</li>
              <li
                ng-click="image.pqe()" tooltip-placement="right"
                uib-tooltip="Provides horizontal and vertical error for points"><a>Enable</a></li>
              <li
                ng-click="image.pqeClear()" tooltip-placement="right"
                uib-tooltip="Clear the PQE information and close the panel"><a>Clear</a></li>
            </ul>
          </li>
          <li
            ng-click="image.viewMetadata(image)" tooltip-placement="right"
            uib-tooltip="View the image metadata"><a>Metadata</a>
          </li>
          <li
            ng-click="image.shareModal()" tooltip-placement="right"
            uib-tooltip="Copy a link to share this image"><a>Share</a>
          </li>
          <li
            ng-click="image.screenshot()" tooltip-placement="right"
            uib-tooltip="Takes a screenshot of the image at current extent and download it as .png"><a>Screenshot</a>
          </li>
          <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
              aria-expanded="false" tooltip-placement="right"
              uib-tooltip="Zoom to full resolution or maximum extent of the current image">Zoom<span class="caret"></span></a>
            <ul class="dropdown-menu">
              <li ng-click="image.zoomToFullRes()"><a>Full Resolution</a></li>
              <li ng-click="image.zoomToFullExtent()"><a>Maximum Extent</a></li>
            </ul>
          </li>
        </ul>
      </div>
      <div class="col-sm-4">
        <form id="searchForm" class="searchForm">
          <div class="input-group input-group-sm">
            <input id="geoJumpInput" type="text" class="form-control" ng-model="image.geoJumpInput" placeholder="BE, Coordinate or Placename" autofocus>
            <span class="input-group-btn">
              <button class="btn btn-info" type="button" ng-click="image.geoJump()">
                <span class="glyphicon glyphicon-search"></span>
              </button>
              <button class="btn btn-default" type="button" ng-click="image.geoJumpInput=''">
                <span class="glyphicon glyphicon-remove"></span>
              </button>
            </span>
          </div>
        </form>
      </div>
    </div>
  </nav>
  <div class="container-fluid">
    <div class="row">
      <!-- Image Map Tools Column -->
      <div class="col-md-3 imageMapTools">
        <!-- Measure Panel -->
        <div class="panel panel-info" ng-show="image.showMeasureInfo">
          <div class="panel-body">
            <small class="text text-info">Measurements</small>
            <br>
            <small class="text text-success">{{image.measureMessage}}</small>
            <br>
            <div class="text-center">
              <small>Measure Type:&nbsp;&nbsp;<span class="text text-info">{{image.measureType}}</span></small>
            </div>
            <div>
              <ui-select
                ng-model="selectedMeasureType.value"
                theme="selectize"
                on-select="image.setMeasureUnits($select.selected.value)">
                <ui-select-match>
                    <span ng-bind="$select.selected.name"></span>
                </ui-select-match>
                <ui-select-choices repeat="item in itemMeasureTypeArray">
                    <span ng-bind="item.name"></span>
                </ui-select-choices>
              </ui-select>
            </div>
            <div class="text-center measure-output">
              <small class="text text-info">Output</small>
            </div>
            <ul>
              <li class="list-group-item">Geodetic Dist.<span class="badge">{{image.geodDist}}</span></li>
              <li class="list-group-item">Rectilinear Dist.<span class="badge">{{image.recDist}}</span></li>
              <li class="list-group-item" ng-show="image.displayAzimuth">Azimuth Bearing<span class="badge">{{image.azimuth}}</span></li>
              <li class="list-group-item" ng-show="image.measurePolygon">Area<span class="badge">{{image.area}}</span></li>
            </ul>
            <div class="text-center">
              <small class="text text-warning">Not certified for targeting</small>
            </div>
          </div>
        </div>
        <!-- Position Quality Evaluator Panel -->
        <div class="panel panel-info" ng-show="image.pqeShowInfo">
          <div class="panel-body">
            <small class="text text-info">Position Quality Evaluator</small>
            <br>
            <small class="text text-success">{{image.pqeMessage}}</small>
            <div ng-show="image.showPqeWarning">
              <div class="alert alert-warning pqe-warning" >
                <small>The current image does not contain the
                  proper metadata to support PQE output.</small>
              </div>
            </div>
            <div ng-show="image.showPqePosOutput">
              <div class="text-center">
                <small class="text text-info text-center">Position</small>
              </div>
              <ul>
                <li class="list-group-item">Lat , Lon<span class="badge">{{image.lat}} , {{image.lon}}</span></li>
                <li class="list-group-item"
                tooltip-placement="bottom"
                uib-tooltip="Height Above Ellipsoid / Height Above Mean Sea Level">HAE / MSL<span class="badge">{{image.hgt}} / {{image.hgtMsl}}</span></li>
                <li class="list-group-item"
                tooltip-placement="bottom"
                uib-tooltip="Image space pixel coordinates">X,Y <small>(pixels)</small><span class="badge">{{image.imageX}} / {{image.imageY}}</span></li>
              </ul>
            </div>
            <div ng-show="image.showPqeOutput">
              <div class="text-center">
                <small class="text text-info text-center">Quality</small>
              </div>
              <ul>
                <li class="list-group-item"
                tooltip-placement="bottom"
                uib-tooltip="Circular Error / Linear Error">CE / LE<span class="badge">{{image.ce}} / {{image.le}}</span></li>
                <li class="list-group-item"
                tooltip-placement="bottom"
                uib-tooltip="Semi-Major Axis / Semi-Minor Axis">SMA / SMI<span class="badge">{{image.sma}} / {{image.smi}}</span></li>
                <li class="list-group-item"
                tooltip-placement="bottom"
                uib-tooltip="Semi-Major Axis Azimuth">SMA AZ<span class="badge">{{image.sma}}  {{image.az}}</span></li>
                <li class="list-group-item"
                tooltip-placement="bottom"
                uib-tooltip="Projection model used to calculate the PQE values">Model<span class="badge">{{image.surfaceName}} / {{image.projType}}</span></li>
              </ul>
            </div>
            <div class="text-center">
              <small class="text text-info"
              tooltip-placement="bottom"
              uib-tooltip="Confidence level % that the point is within the elliptical error identified by the Circular error (CE) and the Linear error (LE)">Probability Level:</small>
            </div>
            <div>
              <ui-select
                ng-model="selectedProbabilityType.value"
                theme="selectize"
                on-select="image.setPqeProbability($select.selected.value)">
                <ui-select-match>
                    <span ng-bind="$select.selected.name"></span>
                </ui-select-match>
                <ui-select-choices repeat="item in pqeProbabilityArray">
                    <span ng-bind="item.name"></span>
                </ui-select-choices>
              </ui-select>
            </div>
            <div class="text-center">
              <small class="text text-warning">Not certified for targeting</small>
              <br>
            </div>
          </div>
        </div>
        <!-- Band Selection Panel -->
        <div class="panel panel-info band-selection-panel">
          <div class="panel-body">
            <div id="band-type" class="image-bands">
              <small class="text text-info">Band Selection</small>
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
            <div id="image-space-bands">
              <div id="gray-image-space-bands" class="image-bands image-band-div">
                <form class="form">
                  <div class="row">
                    <div class="col-sm-4">
                      <div class="form-group">
                        <label for="grayImageItem">Band</label>
                        <ui-select id="grayImageItem" theme="selectize" ng-model="grayImageItem" on-select="onBandSelect($select.selected.value, 'gray')">
                          <ui-select-match>
                              <span ng-bind="$select.selected.value"></span>
                          </ui-select-match>
                          <ui-select-choices repeat="val in bandValues">
                              <span ng-bind="val.value"></span>
                          </ui-select-choices>
                        </ui-select>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
              <form class="form">
                <div id="rgb-image-space-bands" class="row image-bands">
                  <div class="col-sm-4">
                    <div class="form-group">
                      <label for="redImageItem">Red</label>
                      <ui-select id="redImageItem" theme="selectize" ng-model="redImageItem" on-select="onBandSelect($select.selected.value, 'red')">
                        <ui-select-match>{{$select.selected.value}}</ui-select-match>
                        <ui-select-choices repeat="val.key as val in bandValues | filter: $select.search">
                            <span ng-bind-html="val.value | highlight: $select.search"></span>
                        </ui-select-choices>
                      </ui-select>
                    </div>
                  </div>
                  <div class="col-sm-4">
                    <div class="form-group">
                      <label for="greenImageItem">Green</label>
                      <ui-select id="greenImageItem" theme="selectize" ng-model="greenImageItem" on-select="onBandSelect($select.selected.value, 'green')">
                        <ui-select-match>{{$select.selected.value}}</ui-select-match>
                        <ui-select-choices repeat="val.key as val in bandValues | filter: $select.search">
                            <span ng-bind-html="val.value | highlight: $select.search"></span>
                        </ui-select-choices>
                      </ui-select>
                    </div>
                  </div>
                  <div class="col-sm-4">
                    <div class="form-group">
                      <label for="blueImageBand">Blue</label>
                      <ui-select id="blueImageItem" theme="selectize" ng-model="blueImageItem" on-select="onBandSelect($select.selected.value, 'blue')">
                        <ui-select-match>{{$select.selected.value}}</ui-select-match>
                        <ui-select-choices repeat="val.key as val in bandValues | filter: $select.search">
                            <span ng-bind-html="val.value | highlight: $select.search"></span>
                        </ui-select-choices>
                      </ui-select>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
        <!-- Brightness/Contrast Panel -->
        <div class="panel panel-info" id="image-sharpness-contrast">
          <div class="panel-body">
            <div class="row">
              <div class="col-sm-9">
                <div id="brightness-section">
                  <small class="text text-info">Brightness:</small>&nbsp;&nbsp;
                  <span id="imgBrightnessVal"></span><br>
                  <input id="imgBrightnessSlider" data-slider-id="imgBrightnessSlider" type="text"/>
                </div>
                <div id="contrast-section">
                  <small class="text text-info">Contrast:</small>&nbsp;&nbsp;<span id="imgContrastVal"></span><br>
                  <input id="imgContrastSlider" data-slider-id='imgContrastSlider' type="text"/>
                </div>
              </div>
              <div class="col-sm-3">
                <div id="brightnes-contrast-reset" class="brightness-slider">
                  <button type="button" class="btn btn-primary" ng-click="image.resetBrightnessContrast()">Reset</button>
                </div>
              </div>
            </div>
         </div>
        </div>
        <!-- Dynamic Range Adjustment Panel -->
        <div class="panel panel-info">
          <div class="panel-body">
            <small class="text text-info">Dynamic Range Adjustment</small>
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
        <!-- Dynamic Range Region Panel -->
        <div class="panel panel-info">
          <div class="panel-body">
            <small class="text text-info">Dynamic Range Region</small>
            <ui-select
              ng-model="draRegionType"
              on-select="onDraRegionSelect($select.selected.value)"
              theme="selectize">
              <ui-select-match>
                <span ng-bind="$select.selected.name"></span>
              </ui-select-match>
              <ui-select-choices repeat="val in draRegionTypes">
                <span ng-bind="val.name"></span>
              </ui-select-choices>
            </ui-select>
          </div>
        </div>
        <!-- Interpolation Panel -->
        <div class="panel panel-info">
          <div class="panel-body">
            <small class="text text-info">Interpolation</small>
            <ui-select
              ng-model="resamplerFilterType"
              on-select="onResamplerFilterSelect($select.selected.value)"
              theme="selectize">
              <ui-select-match>
                <span ng-bind="$select.selected.name"></span>
              </ui-select-match>
              <ui-select-choices repeat="val in resamplerFilterTypes">
                <span ng-bind="val.name"></span>
              </ui-select-choices>
            </ui-select>
          </div>
        </div>
        <!-- Sharpen Panel -->
        <div class="panel panel-info">
          <div class="panel-body">
            <small class="text text-info">Sharpen Mode</small>
            <ui-select
              ng-model="sharpenModeType"
              on-select="onSharpenModeSelect($select.selected.value)"
              theme="selectize">
              <ui-select-match>
                <span ng-bind="$select.selected.name"></span>
              </ui-select-match>
              <ui-select-choices repeat="val in sharpenModeTypes">
                <span ng-bind="val.name"></span>
              </ui-select-choices>
            </ui-select>
          </div>
        </div>
      </div>
      <!-- Map Column -->
      <div class="col-md-9">
        <div id="imageMap" class="map imageMap imageMapBorder"></div>
      </div>
    </div>
  </div>
</div>
