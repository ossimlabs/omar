<!DOCTYPE html >
<html ng-app="swipeApp">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>O2 | Change Detection Viewer</title>

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
                        <toggle-switch
                                class="toggle toggle-swap" 
                                knob-label="Swap"
                                ng-model="swipe.swapStatus"
                                ng-change="swipe.swap(swipe.layer1, swipe.layer2);">
                            </toggle-switch>
                            <toggle-switch
                                class="toggle toggle-flicker" 
                                knob-label="Flicker"
                                ng-model="swipe.flicker"
                                ng-change="swipe.flickerLayer();">
                            </toggle-switch>
                        <div class="imageOpacity imageOpacity1">
                        <p class="text-center"><strong>Image 1 Opacity</strong></p>
                            <input id="imageOpacity1" type="range" min="0" max="1" step="0.01" value="0" ng-model="swipe.imageOpacity1" ng-change="swipe.imageOpacity1Change()">
                        </div>
                        <div class="imageOpacity imageOpacity2">
                            <p class="text-center"><strong>Image 2 Opacity</strong></p>
                            <input id="image1Opacity2" type="range" min="0" max="1" step="0.01" value="0" ng-model="swipe.imageOpacity2" ng-change="swipe.imageOpacity2Change()">
                        </div>
                        <div id="dd" style="
                            position: absolute;
                            bottom: 35px;
                            left: 45%;
                            z-index: 1;
                            font-size: .7em;
                            color: #fff;
                            background-color: rgba(0,60,136,0.5);
                            padding: 2px 10px;
                            border-radius: 5px;">
                        </div>
                        <div id="dms" style="
                            position: absolute;
                            bottom: 35px;
                            left: 25%;
                            z-index: 1;
                            font-size: .7em;
                            color: #fff;
                            background-color: rgba(0,60,136,0.5);
                            padding: 2px 10px;
                            border-radius: 5px;">
                        </div>
                        <div id="mgrs" style="
                            position: absolute;
                            bottom: 35px;
                            left: 62%;
                            z-index: 1;
                            font-size: .7em;
                            color: #fff;
                            background-color: rgba(0,60,136,0.5);
                            padding: 2px 10px;
                            border-radius: 5px;">
                        </div>
                        <div class="swipe">
                            <p class="text-center"><strong>Swipe Images</strong></p>
                            <input id="swipe" type="range" value="0">
                        </div>
                    </div>
                </div>
            </div> %{-- /.row --}%
        </div>
        <br>
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <h2>Change Detection Viewer</h2>
                <p ng-show="swipe.showHeader" class="text-muted">Enter two image id's, or database id's in input boxes below </p>
                <p ng-show="!swipe.showHeader" class="text-muted">Use the slider on the map above to hide/show the top image.</p>
                <hr class="hr-tight">
            </div>
        </div>
        <div class="row">
            <form class="form">    
                <div class="col-md-4 col-md-offset-2">
                    <div class="form-group">
                        <label>Top Image</label>
                        <input type="text" class="form-control" ng-model="swipe.layer1">
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label>Bottom Image</label>
                        <input type="text" class="form-control" ng-model="swipe.layer2">
                    </div>
                </div>
            </form>
        </div> %{-- /.row --}%
        <br>
        <div class="row">
                <div class="col-md-12 text-center">
                    
                    <button class="btn btn-success btn-sm" ng-click="swipe.addLayer1(swipe.layer1);swipe.addLayer2(swipe.layer2)">Submit</button>

                    <button class="btn btn-warning btn-sm" ng-click="">Reset</button>
        
                </div>
        </div> %{-- /.row --}%
    </div> %{-- /.container-fluid --}%

    <asset:javascript src="swipe.manifest.js"/>
</body>
</html>