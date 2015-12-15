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

    <asset:stylesheet src="app.manifest.css"/>

</head>
    <body>
        <div class="container-fluid">

            <!--- Navigation --->
            <nav class="navbar navbar-inverse" role="navigation">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                            data-target="#main-navbar-collapse" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" ui-sref="#">O2</a>
                </div>
                <div class="collapse navbar-collapse" id="main-navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li><a ui-sref="home">&nbsp;Home</a></li>
                        <li><a ui-sref="map">&nbsp;Map</a></li>
                        <li><a ui-sref="wfs">&nbsp;WFS</a></li>
                        <li><a ui-sref="multiple">&nbsp;Multi</a></li>
                    </ul>
                </div>
            </nav>

            <!--- Main Content --->
            <div ui-view></div>

        </div>

        <asset:javascript src="app.manifest.js"/>

    </body>
</html>