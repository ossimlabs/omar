<%--
  Created by IntelliJ IDEA.
  User: adrake
  Date: 12/10/15
  Time: 10:13 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
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
        <div class="corner-ribbon top-left sticky red shadow">Alpha</div>
        <div class="container-fluid">

            <!--- Navigation --->
            <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                            data-target="#main-navbar-collapse" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    %{--<a class="navbar-brand" ui-sref="#">O2</a>--}%
                    <a class="navbar-brand top-logo" >
                        <asset:image src="o2-logo.png" style="width: 32px; height: 32px;"/>
                    </a>
                </div>
                <div class="collapse navbar-collapse" id="main-navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li><a ui-sref="home">&nbsp;Home</a></li>
                        <li><a ui-sref="map">&nbsp;Map</a></li>
                        <li><a ui-sref="#">&nbsp;Swipe Viewer</a></li>
                        <li><a ui-sref="#">&nbsp;PIWIK</a></li>
                        %{--<li><a ui-sref="wfs">&nbsp;WFS</a></li>--}%
                        %{--<li><a ui-sref="multiple">&nbsp;Multi</a></li>--}%
                    </ul>
                </div>
            </nav>

            <!--- Main Content --->
            <div ui-view></div>

        </div>

        <asset:javascript src="app.manifest.js"/>

    </body>
</html>