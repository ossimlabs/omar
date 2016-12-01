<%@ page contentType="text/html;charset=UTF-8" %>
<html ng-app="omarApp">
<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>O2</title>

  <!-- Hide this line for IE (needed for Firefox and others) -->
  <![if !IE]>
      <asset:link rel="icon" href="favicon.png" type="image/x-icon"/>
  <![endif]>
  <!-- This is needed for IE -->
  <asset:link rel="icon" href="favicon.ico?v=2" type="image/icon"/>

  <asset:stylesheet src="app.manifest.css"/>

    <browser:isMsie>
        <asset:stylesheet src="element.visibility.css"/>
    </browser:isMsie>

</head>
  <body>
    <o2:classificationBanner/>
    <div class="container-fluid">
      <nav style="margin-top: -15px;" class="navbar navbar-inverse" role="navigation" ng-controller="NavController as nav">
        <div class="collapse navbar-collapse" id="main-navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="dropdown">
              <a href = javascript:void(0) class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                <asset:image src="o2-logo.png" style="width: 32px; height: 32px;"/>
                <span class="caret"></span>
              </a>
              <ul class="dropdown-menu">
                <li><a ui-sref="home">&nbsp;Home</a></li>
                <li><a title="Search and discover various types of imagery." ui-sref="map">&nbsp;Map</a></li>
                <li ng-show="{{nav.kmlAppEnabled}}"><a ng-href="{{nav.kmlAppLink}}" title="Download a KML of the last 10 images acquired." target="_blank">&nbsp;KML</a></li>
                <li ng-show="{{nav.piwikAppEnabled}}"><a ng-href="{{nav.piwikAppLink}}" title="View O2 web analytics." target="_blank">&nbsp;PIWIK</a></li>
                <li ng-show="{{nav.tlvAppEnabled}}"><a ng-href="{{nav.tlvAppLink}}" title="An on-demand imagery flipbook<" target="_blank">&nbsp;TLV</a></li>
                <li ng-show="{{nav.userGuideEnabled}}"><a ng-href="{{nav.userGuideLink}}" target="_blank">&nbsp;User Guide</a></li>
                <li ng-show="{{nav.apiAppEnabled}}"><a ng-href="{{nav.apiAppLink}}" target="_blank">&nbsp;API</a></li>
              </ul>
            </li>
            <li class="nav-title-left" ng-bind-html="nav.titleLeft" style="cursor:default"></li>
          </ul>
        </div>
      </nav>
        <div ui-view></div>
    </div>
    <o2:classificationBanner position="bottom" />
    <asset:script>

      var AppO2 = (function () {

        var APP_CONFIG = ${raw( clientConfig.encodeAsJSON() as String )};

        return {

            APP_CONFIG: APP_CONFIG

        }

      })();

    </asset:script>
    <asset:deferredScripts/>
    <asset:javascript src="app.manifest.js"/>

  </body>
</html>
