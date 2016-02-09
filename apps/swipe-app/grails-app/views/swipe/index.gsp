<!DOCTYPE html >
<html ng-app="swipeApp">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>O2 | Swipe</title>

    <!-- Hide this line for IE (needed for Firefox and others) -->
    <![if !IE]>
    <asset:link rel="icon" href="favicon.png" type="image/x-icon"/>
    <![endif]>
    <!-- This is needed for IE -->
    <asset:link rel="icon" href="favicon.ico?v=2" type="image/icon"/>

    <asset:stylesheet src="swipe.manifest.css"/>

</head>

<body>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12">
                <div id="map" class="map"></div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <input id="swipe" type="range">
                <div ng-controller="SwipeController as swipe">

                    %{--<b>URL:</b> <input type="text" ng-model="swipe.url"><br>--}%

                    <b>Layer 1:</b> <input type="text" ng-model="swipe.layer1">
                    <button class="btn btn-primary" ng-click="swipe.addLayer1(swipe.layer1)">add</button>
                    <button class="btn btn-primary" ng-click="swipe.removeLayer1(swipe.layer1)">remove</button><br>

                    <b>Layer 2:</b> <input type="text" ng-model="swipe.layer2">
                    <button class="btn btn-primary" ng-click="swipe.addLayer2(swipe.layer2)">add</button>
                    <button class="btn btn-primary" ng-click="swipe.removeLayer2(swipe.layer2)">remove</button><br>

                    <button class="btn btn-warning" ng-click="swipe.swap(swipe.layer1, swipe.layer2)">swap</button>

                </div>
            </div>
        </div>
    </div>

<asset:javascript src="swipe.manifest.js"/>
</body>
</html>