<nav style="margin-top: -15px;" class="navbar yamm navbar-default" ng-controller="MapController as map">
  <div class="navbar-header">
    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
      data-target="#map-navbar-collapse" aria-expanded="false">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
  </div>
  <div class="container-fluid">
    <div class="row">
      <div class="collapse navbar-collapse" id="map-navbar-collapse">
        <div class="col-sm-8">
          <ul class="nav navbar-nav " ng-controller="FilterController as filter">
            <p class="navbar-text">Filters:</p>
            <li class="dropdown mega-dropdown">
              <a class="dropdown-toggle keyword-filter-dropdown" data-toggle="dropdown" role="button" aria-haspopup="true"
                aria-expanded="false"><span class="fa fa-key" aria-hidden="true"></span>
                 &nbsp;Keyword
               <span class="caret"></span></a>
              <ul class="dropdown-menu mega-dropdown-menu row" ng-click="$event.stopPropagation();">
                <li class="col-sm-12">
                  <ul>
                    <li class="dropdown-header text-center">Keyword Filters</li>
                    <li class="text-center">
                      <p>Click in the input boxes or use the checkboxes next to the keyword parameters to use them
                      as filters</p>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-12">
                  <table style="border-spacing: 0 5" width = "100%">
                    <tr>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.beNumberCheck">
                          </span>
                          <span class="input-group-addon name">BE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                          <input ng-model="filter.beNumber"
                           ng-click="filter.beNumberCheck = true;" class="form-control"
                           ng-blur="filter.beNumberCheck = filter.beNumber === '' ? false: true;"
                           id="beNumberInput"
                           placeholder="Basic Encyclopedia Number"
                           value="filter.beNumber">
                        </div>
                      </td>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.missionIdCheck">
                          </span>
                          <span class="input-group-addon name">Mission</span>
                          <ui-select multiple
                            close-on-select = "true"
                            id = "missionIdInput"
                            ng-blur = "filter.missionIdCheck = filter.missionId === '' ? false : true"
                            ng-click = "filter.missionIdCheck = true; filter.getDistinctValues('missionId');"
                            ng-model = "filter.missionId"
                            theme = "bootstrap">
                            <ui-select-match placeholder = "Mission ID">
                                {{$item}}
                            </ui-select-match>
                            <ui-select-choices repeat = "val in missionIdTypes | filter: $select.search">
                                {{val}}
                            </ui-select-choices>
                          </ui-select>
                        </div>
                      </td class="filter-row">
                    </tr>
                    <tr>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.countryCodeCheck">
                          </span>
                          <span class="input-group-addon name">CC&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                          <ui-select
                           id = "countryCodeInput"
                           ng-blur = "filter.countryCodeCheck = filter.countryCode === '' ? false : true"
                           ng-click = "filter.countryCodeCheck = true; filter.getDistinctValues('countryCode');"
                           ng-model = "filter.countryCode"
                           theme = "selectize">
                            <ui-select-match placeholder = "Country Code">
                              {{$select.selected}}
                            </ui-select-match>
                            <ui-select-choices repeat = "val in countryCodeTypes | filter: $select.search">
                              {{val}}
                            </ui-select-choices>
                          </ui-select>
                        </div>
                      </td>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.sensorIdCheck">
                          </span>
                          <span class="input-group-addon name">Sensor&nbsp;</span>
                          <ui-select multiple
                           id = "sensorIdInput"
                           ng-blur = "filter.sensorIdCheck = filter.sensorId === '' ? false : true"
                           ng-click = "filter.sensorIdCheck = true; filter.getDistinctValues('sensorId');"
                           ng-model = "filter.sensorId"
                           theme = "bootstrap">
                            <ui-select-match placeholder = "Sensor ID">
                              {{$item}}
                            </ui-select-match>
                            <ui-select-choices repeat = "val in sensorIdTypes | filter: $select.search">
                              {{val}}
                            </ui-select-choices>
                          </ui-select>
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.filenameCheck">
                          </span>
                          <span class="input-group-addon name">File&nbsp;&nbsp;&nbsp;</span>
                          <input ng-model="filter.filename"
                           ng-click="filter.filenameCheck = true;"
                           ng-blur="filter.filenameCheck = filter.filename === '' ? false: true;"
                           class="form-control"
                           id="filenameInput"
                           placeholder="File name"
                           value="filter.filename">
                        </div>
                      </td>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.targetIdCheck">
                          </span>
                          <span class="input-group-addon name">Target&nbsp;</span>
                          <ui-select
                           class="form-control"
                           id = "targetIdInput"
                           ng-blur = "filter.targetIdCheck = filter.targetId === '' ? false : true"
                           ng-click = "filter.targetIdCheck = true; filter.getDistinctValues('targetId');"
                           ng-model = "filter.targetId"
                           theme = "selectize">
                            <ui-select-match placeholder = "Target ID">
                              {{$select.selected}}
                            </ui-select-match>
                            <ui-select-choices repeat = "val in targetIdTypes | filter: $select.search">
                              {{val}}
                            </ui-select-choices>
                          </ui-select>
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.imageIdCheck">
                          </span>
                          <span class="input-group-addon name">Image&nbsp;&nbsp;&nbsp;</span>
                          <input ng-model="filter.imageId"
                           ng-click="filter.imageIdCheck = true;"
                           ng-blur="filter.imageIdCheck = filter.imageId === '' ? false: true;"
                           class="form-control"
                           id="imageIdInput"
                           placeholder="Image ID"
                           value="filter.imageId">
                        </div>
                      </td>
                      <td class="filter-row">
                        <div class="input-group input-group-sm">
                          <span class="input-group-addon">
                            <input type="checkbox" ng-model="filter.wacNumberCheck">
                          </span>
                          <span class="input-group-addon name">WAC&nbsp;&nbsp;&nbsp;&nbsp;</span>
                          <input ng-model="filter.wacNumber"
                           ng-click="filter.wacNumberCheck = true;"
                           ng-blur="filter.wacNumberCheck = filter.wacNumber === '' ? false: true;"
                           class="form-control"
                           id="wacNumberInput"
                           placeholder="World Area Code"
                           value="filter.wacNumber">
                        </div>
                      </td>
                    </tr>
                  </table>
                  <br>
                </li>
                <li class="col-sm-12">
                  <ul>
                    <li class="filter-row text-center">
                      <button class="btn btn-primary btn-xs" type="button"
                        ng-click="filter.updateFilterString()">Apply
                      </button>
                      <button class="btn btn-primary btn-xs" type="button"
                        ng-click="filter.initKeywords();">Reset
                      </button>
                      <button class="btn btn-warning btn-xs" type="button"
                        ng-click="filter.closeFilterDropdown('keyword-filter-dropdown')">Close
                      </button>
                    </li>
                  </ul>
                </li>
              </ul>
            </li>
            <li class="dropdown mega-dropdown">
              <a class="dropdown-toggle range-filter-dropdown" data-toggle="dropdown" role="button" aria-haspopup="true"
                aria-expanded="false"><span class="fa fa-sliders" aria-hidden="true"></span>
                &nbsp;Ranges
                <span class="caret"></span></a>
              <ul class="dropdown-menu mega-dropdown-menu row" ng-click="$event.stopPropagation();">
                <li class="col-sm-12">
                  <ul>
                    <li class="dropdown-header text-center">
                      <p class="text-center">Range Filters</p>
                    </li>
                    <li class="text-center">
                      <p>Click the checkbox next to the range parameter below to use it as a filter</p>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.predNiirsCheck">
                        </span>
                        <span class="input-group-addon range-name">NIIRS</span>
                      </div>
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Min</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.predNiirsMin"
                        class="form-control input-sm" id="niirsMin"
                        placeholder="0.0" value="{{filter.predNiirsMin}}">
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Max</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.predNiirsMax"
                       class="form-control input-sm" id="niirsMax"
                       placeholder="9.0" value="{{filter.predNiirsMax}}">
                    </li>
                    <li class="col-sm-3">
                      <small>Valid ranges  0.0 to 9.0</small>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.azimuthCheck">
                        </span>
                        <span class="input-group-addon range-name">Azimuth</span>
                      </div>
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Min</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.azimuthMin"
                       class="form-control input-sm" id="azimuthMin"
                       placeholder="0.0" value="{{filter.azimuthMin}}">
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Max</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.azimuthMax"
                       class="form-control input-sm" id="azimuthMax"
                       placeholder="0.0" value="{{filter.azimuthMax}}">
                    </li>
                    <li class="col-sm-3">
                      <small>Valid ranges 0 to 360 degrees</small>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.grazeElevCheck">
                        </span>
                        <span class="input-group-addon range-name">Graze/Elev</span>
                      </div>
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Min</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.grazeElevMin"
                       class="form-control input-sm" id="grazeElevMin"
                       placeholder="0.0" value="{{filter.grazeElevMin}}">
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Max</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.grazeElevMax"
                       class="form-control input-sm" id="grazeElevMax"
                       placeholder="0.0" value="{{filter.grazeElevMax}}">
                    </li>
                    <li class="col-sm-3">
                      <small>Valid ranges 0 to 90 degrees</small>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.sunAzimuthCheck">
                        </span>
                        <span class="input-group-addon range-name">Sun Azimuth</span>
                      </div>
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Min</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.sunAzimuthMin"
                       class="form-control input-sm" id="sunAzimuthMin"
                       placeholder="0.0" value="{{filter.sunAzimuthMin}}">
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Max</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.sunAzimuthMax"
                       class="form-control input-sm" id="sunAzimuthMax"
                       placeholder="9.0" value="{{filter.sunAzimuthMax}}">
                    </li>
                    <li class="col-sm-3">
                      <small>Valid ranges 0 to 360 degrees</small>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.sunElevationCheck">
                        </span>
                        <span class="input-group-addon range-name">Sun Elevation</span>
                      </div>
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Min</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.sunElevationMin"
                       class="form-control input-sm" id="sunElevationMin"
                       placeholder="0.0" value="{{filter.sunElevationMin}}">
                    </li>
                    <li class="col-sm-1 visible-xs">
                      <label class="label label-primary">Max</label>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.sunElevationMax"
                       class="form-control input-sm" id="sunElevationMax"
                       placeholder="90.0" value="{{filter.sunElevationMax}}">
                    </li>
                    <li class="col-sm-3">
                      <small>Valid ranges -90 to 90 degrees</small>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.cloudCoverCheck">
                        </span>
                        <span class="input-group-addon range-name">Cloud Cover</span>
                      </div>
                    </li>
                    <li class="col-sm-2">
                      <input ng-model="filter.cloudCover"
                       class="form-control input-sm" id="cloudCover"
                       placeholder="10.0" value="{{filter.cloudCover}}">
                    </li>
                    <li class="col-sm-2">
                    </li>
                    <li class="col-sm-3">
                      <small>Valid ranges 0 to 100%</small>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-12 text-center">
                      <button class="btn btn-primary btn-xs" type="button"
                        ng-click="filter.updateFilterString()">Apply
                      </button>
                      <button class="btn btn-warning btn-xs" type="button"
                        ng-click="filter.initRanges();">Reset
                      </button>
                      <button class="btn btn-warning btn-xs" type="button"
                        ng-click="filter.closeFilterDropdown('range-filter-dropdown')">Close
                      </button>
                    </li>
                  </ul>
                </li>
              </ul>
            </li><!-- End menu -->
            <li class="dropdown mega-dropdown">
              <a class="dropdown-toggle spatial-filter-dropdown" data-toggle="dropdown" role="button" aria-haspopup="true"
                aria-expanded="false"><span class="fa fa-map" aria-hidden="true"></span>
                &nbsp;Spatial
                <span class="caret"></span></a>
              <ul class="dropdown-menu mega-dropdown-menu row" ng-click="$event.stopPropagation();">
                <li class="col-sm-12">
                  <ul>
                    <li class="dropdown-header text-center">
                      <p class="text-center">Spatial Filters</p>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.viewPortSpatial"
                          ng-change="filter.byViewPort(filter.viewPortSpatial)">
                        </span>
                        <span class="input-group-addon spatial-name">Map Viewport</span>
                      </div>
                    </li>
                    <li class="col-sm-9">
                      <p>This filter is on by default.  It constrains the
                        query to the boundaries of the current map extent</p>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.pointSpatial"
                          ng-change="filter.byPointer(filter.pointSpatial)">
                        </span>
                        <span class="input-group-addon spatial-name">Point</span>
                      </<div>
                    </li>
                    <li class="col-sm-9">
                      <p>Single clicking on the map
                        will return a potential list of images at that location</p>
                    </li>
                    <li class="col-sm-12">
                      <hr>
                    </li>
                    <li class="col-sm-3 filter-row">
                      <div class="input-group input-group-sm">
                        <span class="input-group-addon">
                          <input type="checkbox"
                          ng-model="filter.polygonSpatial"
                          ng-change="filter.byPolygon(filter.polygonSpatial)">
                        </span>
                        <span class="input-group-addon spatial-name">Polygon</span>
                      </div>
                    </li>
                    <li class="col-sm-9 filter-row">
                      <p>Left-click and hold with the
                        ALT key to create a box that will return a potential list of images
                      </p>
                    </li>
                    <li class="col-sm-12 text-center">
                      <button class="btn btn-warning btn-xs" type="button"
                        ng-click="filter.closeFilterDropdown('spatial-filter-dropdown')">Close
                      </button>
                    </li>
                  </ul>
                </li>
              </ul>
            </li>
            <li class="dropdown mega-dropdown">
              <a  class="dropdown-toggle temporal-filter-dropdown" data-toggle="dropdown" role="button" aria-haspopup="true"
               aria-expanded="false"><span class="fa fa-clock-o" aria-hidden="true"></span>
               &nbsp;Temporal<span class="caret"></span></a>
              <ul class="dropdown-menu mega-dropdown-menu row" ng-click="$event.stopPropagation();">
                <li class="col-sm-12">
                  <ul>
                    <li class="dropdown-header text-center">Temporal Filters</li>
                    <li class="text-center">
                      <p>Select a date type and duration filter from the select boxes below.  Changes will be reflected immediately</p>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-6">
                  <ul>
                    <li class="filter-row">
                      <div class="form-group form-group-sm">
                        <label for="temporalTypeFilter">Date Type</label>
                        <select ng-model="filter.currentDateType"
                          ng-options="type.label for type in filter.dateTypes"
                          id="temporalTypeFilter"
                          class="form-control">
                        </select>
                      </div>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-6">
                  <ul>
                    <li class="filter-row">
                      <div class="form-group form-group-sm">
                        <label for="temporalDuration">Duration</label>
                        <select ng-model="filter.currentTemporalDuration"
                          ng-options="duration.label for duration in filter.temporalDurations"
                          ng-change="filter.updateFilterString()"
                          id="temporalDuration"
                          class="form-control">
                        </select>
                      </div>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-6" ng-show="filter.customDateRangeVisible">
                  <ul>
                    <li class="filter-row">
                      <p class="text-center">Start Time & Date</p>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-6" ng-show="filter.customDateRangeVisible">
                  <ul>
                    <li>
                      <p class="text-center">End Time & Date</p>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-6" ng-show="filter.customDateRangeVisible">
                  <ul>
                    <li class="filter-row text-center">
                      <div class="form-group form-group-sm">
                        <input type="text" class="form-control"
                         ng-model="filter.startDate"
                         data-time-format="HH:mm:ss"
                         data-autoclose="false"
                         data-minute-step="1"
                         data-second-step="1"
                         placeholder="Time" bs-timepicker>
                      </div>
                      <div style="display:inline-block;">
                        <uib-datepicker
                          ng-model="filter.startDate"
                          show-weeks="false"
                          class="well well-sm">
                        </uib-datepicker>
                      </div>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-6" ng-show="filter.customDateRangeVisible">
                  <ul>
                    <li class="filter-row text-center">
                      <div class="form-group form-group-sm">
                        <input type="text" size="8" class="form-control"
                         ng-model="filter.endDate"
                         data-time-format="HH:mm:ss"
                         data-autoclose="0" placeholder="Time" bs-timepicker>
                      </div>
                      <div style="display:inline-block;">
                        <uib-datepicker
                          ng-model="filter.endDate"
                          show-weeks="false"
                          class="well well-sm">
                        </uib-datepicker>
                      </div>
                    </li>
                  </ul>
                </li>
                <li class="col-sm-12 text-center">
                  <button class="btn btn-primary btn-xs" type="button"
                    ng-click="filter.updateFilterString()">Apply
                  </button>
                  <button class="btn btn-warning btn-xs" type="button"
                    ng-click="filter.closeFilterDropdown('temporal-filter-dropdown')">Close
                  </button>
                </li>
              </ul><!-- end menu -->
            </li>
          </ul>
        </div>
        <div class="col-sm-4">
          <form id="searchForm" class="searchForm">
            <div class="input-group input-group-sm" ng-controller="SearchController as search">
              <input id="searchInput" type="text" ng-model="search.searchInput" class="form-control" placeholder="Search O2" autofocus>
              <span class="input-group-btn">
                <button class="btn btn-info" type="button" ng-click="search.executeSearch()" ng-disabled="search.searchButtonDisabled"><span class="glyphicon glyphicon-search"></span></button>
                <button class="btn btn-default" type="button" ng-click="search.resetSearchInput()"><span class="glyphicon glyphicon-remove"></span></button>
              </span>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</nav>
<div style="margin-top: -15px;" class="row">
  <div class="col-md-8">
    <div id="map" class="map" params="map.mapParams" map></div>
      <div id="mouseCoords" class="map-cord-div" tooltip-placement="top"
      uib-tooltip="Click on the coordinates to change units." tooltip-popup-delay="300"></div>
      <div id="popup" class="ol-popup">
        <div id="popup-content"></div>
      </div>
  </div>
  <div class="col-md-4" ng-controller="ListController as list">
    <div class="visible-xs-block visible-sm-block">
      <hr>
    </div>
    <nav class="navbar navbar-inverse">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
            data-target="#sort-navbar-collapse" aria-expanded="false">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
          </button>
          <p class="navbar-text">Sort:</p>
        </div>
        <div class="collapse navbar-collapse" id="sort-navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="dropdown">
              <a class="dropdown-toggle" data-toggle="dropdown" role="button"
               aria-haspopup="true" aria-expanded="false">
               {{list.currentSortText}}<span class="caret"></span></a>
              <ul class="dropdown-menu">
                <li ng-click="list.sortWfs('acquisition_date', '+D', 'Acquired (New)');"><a>Acquired (New)</a></li>
                <li ng-click="list.sortWfs('acquisition_date', '+A', 'Acquired (Old)');"><a>Acquired (Old)</a></li>
                <li role="separator" class="divider"></li>
                <li ng-click="list.sortWfs('ingest_date', '+D', 'Ingest (New)');"><a>Ingested (New)</a></li>
                <li ng-click="list.sortWfs('ingest_date', '+A', 'Ingest (Old)');"><a>Ingested (Old)</a></li>
                <li role="separator" class="divider"></li>
                <li ng-click="list.sortWfs('title', '+D', 'Image ID (Desc)');"><a>Image ID (Desc)</a></li>
                <li ng-click="list.sortWfs('title', '+A', 'Image ID (Asc)');"><a>Image ID (Asc)</a></li>
                <li role="separator" class="divider"></li>
                <li ng-click="list.sortWfs('sensor_id', '+A', 'Sensor (Asc)');"><a>Sensor (Asc)</a></li>
                <li ng-click="list.sortWfs('sensor_id', '+D', 'Sensor (Desc)');"><a>Sensor (Desc)</a></li>
                <li role="separator" class="divider"></li>
                <li ng-click="list.sortWfs('mission_id', '+A', 'Mission (Asc)');"><a>Misson (Asc)</a></li>
                <li ng-click="list.sortWfs('mission_id', '+D', 'Mission (Desc)');"><a>Misson (Desc)</a></li>
              </ul>
            </li>
            <li class="dropdown nav-download">
              <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Export
                <span class="caret"></span>
              </a>
              <ul class="dropdown-menu" ng-controller="WFSOutputDlController as wfsOutputDownload">
                <li><a ng-href="" target="_blank" ng-click="wfsOutputDownload.getDownloadURL('CSV')">CSV</a></li>
                <li><a ng-href="" target="_blank" ng-click="wfsOutputDownload.getDownloadURL('GML2')">GML2</a></li>
                <li><a ng-href="" target="_blank" ng-click="wfsOutputDownload.getDownloadURL('GML3')">GML3</a></li>
                <li><a ng-href="" target="_blank" ng-click="wfsOutputDownload.getDownloadURL('GML32')">GML32</a></li>
                <li><a ng-href="" target="_blank" ng-click="wfsOutputDownload.getDownloadURL('JSON')">JSON</a></li>
                <li><a ng-href="" target="_blank" ng-click="wfsOutputDownload.getDownloadURL('KML')">KML</a></li>
                <li><a ng-href="" target="_blank" ng-click="wfsOutputDownload.goToTLV()">TLV</a></li>
              </ul>
            </li>
          </ul>
          <p class="navbar-text pull-right">&nbsp;&nbsp;&nbsp;&nbsp;
            <span class="label label-primary">{{list.wfsFeatures}}</span>
          </p>
        </div><!-- /.navbar-collapse -->
      </div><!-- /.container-fluid -->
    </nav>
    <div id="list" style="border-style: solid; border-width: 1px; padding: 10px; border-radius: 4px;">
      <div ng-show="list.wfsData.length === 0">
        <div>
          <span class="text-default"><h4 class="text-center"><strong>We did not find any images that match your search filters</strong></h4></span>
          <span class="text-info"><h4 >Check the dates</h4></span>
          <p>Make sure you provide valid dates for the query.  Also, make sure you are searching for the
            appropriate date type (acquisition versus ingest).</p>
          <span class="text-info"><h4>Check the spelling</h4></span>
          <p>It is possible that one of the Keyword filters has a spelling error.</p>
          <span class="text-info"><h4>Check range values</h4></span>
          <p>Make sure that the range values you have submitted are valid for those attributes.</p>
          <span class="text-info"><h4 class="text-info">Check your map extent</h4></span>
          <p>The map extent is also a filter for the images.  Make sure the map is zoomed out to an
            appropriate extent for your search.</p>
        </div>
      </div>
      <!-- <div ng-show="list.wfsData.length >= 1"
       ng-repeat="image in list.wfsData.slice(((list.currentStartIndex-1)*list.itemsPerPage), ((list.currentStartIndex)*list.itemsPerPage))" ng-init="list.showProcessInfo=[]"
       ng-model="image"> -->
      <div ng-show="list.wfsFeatures >= 1"
        ng-repeat="image in list.wfsData" ng-init="list.showProcessInfo=[]"
        ng-model="image">
        <div class="panel panel-default" >
          <div class="panel-body"
           ng-mouseenter="list.displayFootprint(image);"
           ng-mouseleave="list.removeFootprint();">
            <div class="media">
              <div class="media-left">
                <img ng-style="list.thumbBorder(image.properties.file_type)"
                  class="media-object"
                  ng-click="list.showImageModal(image, list.imageSpaceDefaults); list.logRatingToPio(image.properties.id);"
                  tooltip-placement="right"
                  uib-tooltip="Click the thumbnail or the Image ID to preview image and view metadata"
                  ng-src="{{list.thumbPath}}?{{list.thumbFilename}}{{image.properties.filename}}{{list.thumbEntry}}{{image.properties.entry_id}}{{list.thumbSize}}{{list.thumbFormat}}"
                  alt="Image thumbnail"
                  style="cursor: pointer;">&nbsp;
                <span class="text-center" ng-show="list.showProcessInfo[$index]">
                  <span style="font-size: .8em">&nbsp;{{list.processType}}&nbsp;&nbsp;</span><i class="fa fa-cog fa-spin text-info"></i>
                </span>
              </div>
              <div class="media-body">
                <div class="row">
                  <div class="col-md-12" style="font-size: 13px;">
                    ID:&nbsp;&nbsp;
                    <span ng-click="list.showImageModal(image, list.imageSpaceDefaults); list.logRatingToPio(image.properties.id);"
                      class="text-success image-id-link">
                      <span ng-show="!image.properties.title">Unknown</span>
                      {{image.properties.title}}
                    </span>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-12" style="font-size: 13px;">
                    Acquisition Date:&nbsp;&nbsp;
                    <span class="text-success">
                      <span ng-show="!image.properties.acquisition_date">Unknown</span>
                      {{image.properties.acquisition_date | date:'MM/dd/yyyy HH:mm:ss' : 'UTC'}}
                      <span ng-show="image.properties.acquisition_date">z</span>
                    </span>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-12" style="font-size: 13px;">
                    NIIRS:&nbsp;&nbsp;
                    <span class="text-success">
                      <span ng-show="!image.properties.niirs">Unknown</span>
                      {{image.properties.niirs}}
                    </span>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-12" style="font-size: 13px;">
                    Sensor ID:&nbsp;&nbsp;
                    <span class="text-success">
                      <span ng-show="!image.properties.sensor_id">Unknown</span>
                      {{image.properties.sensor_id}}
                    </span>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-12" style="font-size: 13px;">
                    Mission ID:&nbsp;&nbsp;
                    <span class="text-success">
                      <span ng-show="!image.properties.mission_id">Unknown</span>
                      {{image.properties.mission_id}}
                    </span>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-12">
                    <p class="text-primary" style="margin-top:.2em;">
                      <a href="{{list.o2baseUrl}}/#/mapImage?filename={{image.properties.filename}}&entry_id={{image.properties.entry_id}}&width={{image.properties.width}}&height={{image.properties.height}}&bands={{list.imageSpaceDefaults.bands}}&numOfBands={{image.properties.number_of_bands}}&imageId={{image.properties.id}}&brightness={{list.imageSpaceDefaults.brightness}}&contrast={{list.imageSpaceDefaults.contrast}}&histOp={{list.imageSpaceDefaults.histOp}}&histCenterTile={{list.imageSpaceDefaults.histCenterTile}}&resamplerFilter={{list.imageSpaceDefaults.resamplerFilter}}&sharpenMode={{list.imageSpaceDefaults.sharpenMode}}" target="_blank">
                        <i class="fa fa-desktop fa-border text-primary"
                         style="cursor: pointer;"
                         tooltip-placement="right"
                         uib-tooltip="View raw image"></i>&nbsp;&nbsp;
                      </a>
                      <a href="" ng-click = "list.viewOrtho(image)" target="_blank">
                        <i class="fa fa-history fa-border text-primary"
                         style="cursor: pointer;"
                         tooltip-placement="right"
                         uib-tooltip="View rectified image in TLV"></i>&nbsp;&nbsp;
                      </a>
                      <a ng-show="{{list.kmlSuperOverlayAppEnabled}}" href="{{list.kmlSuperOverlayLink}}/superOverlay/createKml/{{image.properties.id}}">
                        <i class="fa fa-map fa-border text-primary"
                         style="cursor: pointer;"
                         tooltip-placement="right"
                         uib-tooltip="Download KML"></i>&nbsp;&nbsp;
                      </a>
                      <a ng-href="" target="_blank" ng-click="list.shareModal(list.getImageSpaceUrl(image))">
                        <i class="fa fa-share-alt fa-border text-primary"
                         style="cursor: pointer;"
                         tooltip-placement="right"
                         uib-tooltip="Share link"></i>&nbsp;&nbsp;
                      </a>
                      <a ng-href="" target="_blank" ng-click="list.archiveDownload(image.properties.id)">
                        <i class="fa fa-download fa-border text-primary"
                          style="cursor: pointer;"
                          tooltip-placement="right"
                          uib-tooltip="Download"></i>&nbsp;&nbsp;
                      </a>
                      <a ng-show="{{list.jpipAppEnabled}}" href="" ng-click="list.getJpipStream($event, image.properties.filename, image.properties.entry_id, 'chip', $index, 'stream');">
                        <i class="fa fa-file-image-o fa-border text-primary"
                         style="cursor: pointer;"
                         tooltip-placement="top"
                         uib-tooltip="JPIP image"></i>&nbsp;&nbsp;
                      </a>
                      <a ng-show="{{list.jpipAppEnabled}}" href="" ng-click="list.getJpipStream($event, image.properties.filename, image.properties.entry_id, '4326', $index, 'ortho');">
                        <i class="fa fa-image fa-border text-primary"
                         style="cursor: pointer;"
                         tooltip-placement="top"
                         uib-tooltip="JPIP ortho"></i>&nbsp;&nbsp;
                      </a>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
    <!-- right-click context menu -->
    <div class="modal" id="contextMenuDialog" role="dialog" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4>You Clicked Here:</h4></div>
                <div align="center" class="modal-body"></div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-warning" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <div class="text-center">
      <uib-pagination style="margin: 8px;"
        total-items="list.wfsFeaturesTotalPaginationCount"
        items-per-page="list.pageLimit"
        ng-model="list.currentStartIndex"
        ng-change="list.pagingChanged()"
        max-size="5"
        class="pagination-sm" previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;">
      </uib-pagination>
    </div>
  </div>
</div>
