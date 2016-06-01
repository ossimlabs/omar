<%--
  Created by IntelliJ IDEA.
  User: adrake
  Date: 12/10/15
  Time: 10:13 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html ng-app="omarApp">
%{-- <html> --}%
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

</head>
    <body>
        <o2:classificationBanner/>
        <div class="container-fluid">

            <nav style="margin-top: -15px;" class="navbar navbar-inverse" role="navigation" ng-controller="NavController as nav">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                            data-target="#main-navbar-collapse" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand top-logo" >
                        <asset:image src="o2-logo.png" style="width: 32px; height: 32px;"/>
                    </a>
                </div>
                <div class="collapse navbar-collapse" id="main-navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li><a ui-sref="home">&nbsp;Home</a></li>
                        <li><a ui-sref="map">&nbsp;Map</a></li>
                        <li ng-show="{{nav.swipeAppEnabled}}"><a ng-href="{{nav.swipeAppLink}}" target="_blank">&nbsp;Swipe</a></li>
                        <li ng-show="{{nav.piwikAppEnabled}}"><a ng-href="{{nav.piwikAppLink}}" target="_blank">&nbsp;PIWIK</a></li>
                        <li ng-show="{{nav.wmtsAppEnabled}}"><a ng-href="{{nav.wmtsAppLink}}" target="_blank">&nbsp;WMTS</a></li>
                        <li ng-show="{{nav.apiAppEnabled}}"><a ng-href="{{nav.apiAppLink}}" target="_blank">&nbsp;API</a></li>
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
