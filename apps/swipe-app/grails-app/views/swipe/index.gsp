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
    <o2:classificationBanner/>
    <div class="container">
        <div class="jumbotron imageheader" ng-show="swipe.showHeader">
            <div class="col-md-12">
                <h1 class="text-center"><i class="fa fa-exchange"></i>&nbsp;&nbsp;Swipe Viewer</h1>
            </div>
        </div>
    </div>
    <div class="container-fluid">
        <div id="fullscreen" class="fullscreen">
            <div class="row" ng-show="!swipe.showHeader">
                <div class="col-md-12">
                    <div id="map" class="map">         
                        <div class="toggle-swap-container">
                            <toggle-switch
                                class="toggle toggle-swap" 
                                knob-label="Swap"
                                ng-model="swipe.swapStatus"
                                ng-change="swipe.swap(swipe.layer1, swipe.layer2);">
                            </toggle-switch>
                        </div> 
                        <div class="toggle-flicker-container">  
                            <toggle-switch
                                class="toggle toggle-flicker" 
                                knob-label="Flicker"
                                ng-model="swipe.flicker"
                                ng-change="swipe.flickerLayer();">
                            </toggle-switch>
                        </div>
                        <div class="imageOpacity imageOpacity1-container">
                            <div class="imageOpacity1">
                            <p class="text-center"><strong>Image 1 Opacity</strong></p>
                                <input id="imageOpacity1" type="range" min="0" max="1" step="0.01" value="0" ng-model="swipe.imageOpacity1" ng-change="swipe.imageOpacity1Change()">
                            </div>
                        </div>
                        <div class="imageOpacity imageOpacity2-container">
                            <div class="imageOpacity2">
                                <p class="text-center"><strong>Image 2 Opacity</strong></p>
                                <input id="image1Opacity2" type="range" min="0" max="1" step="0.01" value="0" ng-model="swipe.imageOpacity2" ng-change="swipe.imageOpacity2Change()">
                            </div>
                        </div>
                        <div class="dd-container">
                            <div id="dd" class="dd"></div>
                        </div>
                        <div class="dms-container">
                            <div id="dms" class="dms"></div>
                        </div>
                        <div class="mgrs-container">
                            <div id="mgrs" class="mgrs"></div>
                        </div>
                        <div class="swipe-container">
                            <div class="swipe">
                                <p class="text-center"><strong>Swipe Images</strong></p>
                                <input id="swipe" type="range" value="0">
                            </div>
                        </div>
                    </div>
                </div>
            </div> %{-- /.row --}%
        </div>
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="well" ng-show="swipe.showHeader">
                    <h4>This application allows you to select two images from the O2 database, and swipe the top image over the bottom image</h4>
                    <h5><i class="fa fa-expand text-info"></i>&nbsp;&nbsp;Use the fullscreen button to utilize your entire desktop while viewing the images</h5>
                    <h5><i class="fa fa-rotate-left text-info"></i>&nbsp;&nbsp;Rotate the images by using the hotkeys ALT + SHIFT</h5>
                    <h5><i class="fa fa-fast-forward text-info"></i>&nbsp;&nbsp;Use the Flicker toggle to have the images to turn on/off in rapid succession</h5>
                    <h5><i class="fa fa-random text-info"></i>&nbsp;&nbsp;The Swap toggle can be used to change the rendering order of the images</h5>
                    <h5><i class="fa fa-toggle-on text-info"></i>&nbsp;&nbsp;Increase or decrease the image opacity by using their sliders</h5>
                </div>
                <p ng-show="swipe.showHeader" class="text-muted">Enter two image id's in input boxes below and click the submit button </p>
                
                <p ng-show="!swipe.showHeader" class="text-muted">Use the slider on the map above to hide/show the top image</p>
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
                    
                    <button class="btn btn-primary" ng-click="swipe.addLayer1(swipe.layer1, false);">Submit</button>

                    <button class="btn btn-warning" ng-click="swipe.resetLayers();">Reset</button>
        
                </div>
        </div> %{-- /.row --}%
    </div> %{-- /.container-fluid --}%
    <o2:classificationBanner position="bottom" />
    
    <g:javascript>
        var initParams = ${raw( initParams.toString() )};
        console.log('initParams: ', initParams);
        // var APP_CONFIG = {
        //     wfs: 'blah',
        //     wms: 'blah'
        // };
    </g:javascript>
    <asset:javascript src="swipe.manifest.js"/>

</body>
</html>