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

<body ng-controller="SwipeController as swipe">
    <div class="container-fluid">
        <div class="row imageheader" ng-show="swipe.showHeader">
            <div class="col-md-12">
                Swipe image header here...
            </div>
        </div>
        <div id="fullscreen" class="fullscreen">
            <div class="row" ng-show="!swipe.showHeader">
                <div class="col-md-12">
                    <div id="map" class="map">
                        <div class="swipe">
                            <input id="swipe" type="range" value="0">
                        </div>
                    </div>
                </div>
            </div> %{-- /.row --}%
        </div>
        <br>
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <h2>Swipe Viewer</h2>
                <p class="text-muted">Enter two image id's, or database id's in input boxes below </p>
                <hr class="hr-tight">
            </div>
        </div>
        <div class="row">
            <form class="form">    
                <div class="col-md-4 col-md-offset-2">
                    <div class="form-group">
                        <label>Image 1</label>
                        <input type="text" class="form-control" ng-model="swipe.layer1">
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label>Image 2</label>
                        <input type="text" class="form-control" ng-model="swipe.layer2">
                    </div>
                </div>
            </form>
        </div> %{-- /.row --}%
        <br>
        <div class="row">
                <div class="col-md-12 text-center">
                    
                    <button class="btn btn-success btn-sm" ng-click="swipe.addLayer1(swipe.layer1);swipe.addLayer2(swipe.layer2)">Submit</button>

                    <button class="btn btn-warning btn-sm" ng-click="">Reset</button><br><br>
                    
                    <button class="btn btn-info btn-sm" ng-click="swipe.swap(swipe.layer1, swipe.layer2);">Swap</button><br><br>
                    %{-- <button class="btn btn-info btn-sm" ng-click="swipe.flickerLayer();">Flicker</button> --}%
                    <label>Flicker image</label>
                    <toggle-switch ng-model="swipe.flicker" ng-change="swipe.flickerLayer();"></toggle-switch>
                    
                    
                </div>
        </div> %{-- /.row --}%
    </div> %{-- /.container-fluid --}%

    <asset:javascript src="swipe.manifest.js"/>
</body>
</html>