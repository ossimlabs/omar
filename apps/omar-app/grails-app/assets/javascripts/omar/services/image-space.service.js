(function() {
    'use strict';
    angular
        .module('omarApp')
        .service('imageSpaceService', ['$http', 'stateService', imageSpaceService]);

    function imageSpaceService($http, stateService) {

        // #################################################################################
        // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
        // provides access to various client params in application.yml
        // #################################################################################

        var map,
            filename,
            entry,
            format,
            imgWidth,
            imgHeight,
            tileX,
            tileY,
            tileZ,
            imgCenter,
            proj,
            source,
            source2,
            upAngle,
            northAngle,
            bands,
            numOfBands;

        var ImageSpaceTierSizeCalculation = {
            DEFAULT: 'default',
            TRUNCATED: 'truncated'
        };

        var RotateNorthControl = function(opt_options) {
            var options = opt_options || {};
            var span = document.createElement('span');
            span.className = 'ol-compass';
            span.textContent = '\u21E7';

            var button = document.createElement('button');
            button.appendChild(span);
            button.title = 'North is Up';

            var this_ = this;

            var handleRotateNorth = function(e) { console.dir(northAngle);
                this_.getMap().getView().setRotation(northAngle);
                console.log('handleRotateNorth', northAngle);
            };

            button.addEventListener('click', handleRotateNorth, false);
            button.addEventListener('touchstart', handleRotateNorth, false);

            var element = document.createElement('div');

            element.className = 'rotate-north ol-unselectable ol-control';
            element.appendChild(button);

            ol.control.Control.call(this, {
                element: element,
                target: options.target
            });

        };
        ol.inherits(RotateNorthControl, ol.control.Control);

        function rotateNorthArrow(radians) {
            var transform = 'rotate(' + radians + 'rad)';
            var arrow = $('.ol-compass');
            arrow.css('msTransform', transform);
            arrow.css('transform', transform);
            arrow.css('webkitTransform', transform);
        }

        var RotateUpControl = function(opt_options) {

            var options = opt_options || {};
            var button = document.createElement('button');

            button.innerHTML = 'U';
            button.title = 'Up is Up';

            var this_ = this;

            var handleRotateUp = function(e) {
                console.log('handleRotateUp', upAngle);
                this_.getMap().getView().setRotation(upAngle);
            };

            button.addEventListener('click', handleRotateUp, false);
            button.addEventListener('touchstart', handleRotateUp, false);

            var element = document.createElement('div');

            element.className = 'rotate-up ol-unselectable ol-control';
            element.appendChild(button);

            ol.control.Control.call(this, {
                element: element,
                target: options.target
            });

        };
        ol.inherits(RotateUpControl, ol.control.Control);

        var ImageSpace = function(opt_options) {
            var options = opt_options || {};

            var size = options.size;
            var tierSizeCalculation = options.tierSizeCalculation !== undefined ?
                options.tierSizeCalculation :
                ImageSpaceTierSizeCalculation.DEFAULT;

            filename = options.filename;
            entry = options.entry;
            format = options.format;
            upAngle = options.upAngle;
            northAngle = options.northAngle;

            var imageWidth = size[0];
            var imageHeight = size[1];
            var tierSizeInTiles = [];
            var tileSize = ol.DEFAULT_TILE_SIZE || 256;

            switch (tierSizeCalculation) {
                case ImageSpaceTierSizeCalculation.DEFAULT:
                    while (imageWidth > tileSize || imageHeight > tileSize) {
                        tierSizeInTiles.push([
                            Math.ceil(imageWidth / tileSize),
                            Math.ceil(imageHeight / tileSize)
                        ]);
                        tileSize += tileSize;
                    }
                    break;
                case ImageSpaceTierSizeCalculation.TRUNCATED:
                    var width = imageWidth;
                    var height = imageHeight;
                    while (width > tileSize || height > tileSize) {
                        tierSizeInTiles.push([
                            Math.ceil(width / tileSize),
                            Math.ceil(height / tileSize)
                        ]);
                        width >>= 1;
                        height >>= 1;
                    }
                    break;
                default:
                    goog.asserts.fail();
                    break;
            }

            tierSizeInTiles.push([1, 1]);
            tierSizeInTiles.reverse();

            var resolutions = [1];
            var tileCountUpToTier = [0];
            var i = 1,
                ii = tierSizeInTiles.length;
            while (i < ii) {
                resolutions.push(1 << i);
                tileCountUpToTier.push(
                    tierSizeInTiles[i - 1][0] * tierSizeInTiles[i - 1][1] +
                    tileCountUpToTier[i - 1]
                );
                i++
            }

            resolutions.reverse();

            var extent = [0, -size[1], size[0], 0];
            var tileGrid = new ol.tilegrid.TileGrid({
                extent: extent,
                origin: ol.extent.getTopLeft(extent),
                resolutions: resolutions
            });

            var url = options.url;

            /**
             * @this {ol.source.TileImage}
             * @param {ol.TileCoord} tileCoord Tile Coordinate.
             * @param {number} pixelRatio Pixel ratio.
             * @param {ol.proj.Projection} projection Projection.
             * @return {string|undefined} Tile URL.
             */
            function tileUrlFunction(tileCoord, pixelRatio, projection) {
                if (!tileCoord) {
                    return undefined;
                } else {
                    tileZ = tileCoord[0];
                    tileX = tileCoord[1];
                    tileY = -tileCoord[2] - 1;

                    return url + '?filename=' + filename + '&entry=' + entry + '&z=' + tileZ +
                        '&x=' + tileX + '&y=' + tileY + '&format=' + format +
                        '&numOfBands=' + numOfBands + '&bands=' + bands;
                }
            }

            ol.source.TileImage.call(this, {
                attributions: options.attributions,
                crossOrigin: options.crossOrigin,
                logo: options.logo,
                reprojectionErrorThreshold: options.reprojectionErrorThreshold,
                tileClass: ol.source.ZoomifyTile,
                tileGrid: tileGrid,
                tileUrlFunction: tileUrlFunction
            });
        };

        ol.inherits(ImageSpace, ol.source.TileImage);

        this.initImageSpaceMap = function(params, isModal) {

            // set header title
            var wfsUrl = AppO2.APP_CONFIG.params.wfs.baseUrl +
                "filter=filename LIKE '" + params.filename + "'" +
                "&outputFormat=JSON" +
                "&request=GetFeature" +
                "&service=WFS" +
                "&typeName=omar:raster_entry" +
                "&version=1.1.0";
            $http({
                method: 'GET',
                url: encodeURI(wfsUrl)
            }).then(function(response) {
                    var properties = response.data.features[0].properties;
                    var imageId = properties.title || properties.filename;
                    var acquisitionDate = properties.acquisition_date || "";

                    if (!isModal) {
                        stateService.navStateUpdate({ titleLeft: imageId + " <br> " + acquisitionDate });
                    }
            });

            filename = params.filename;
            entry = params.entry;
            imgWidth = params.imgWidth;
            imgHeight = params.imgHeight;
            numOfBands = params.numOfBands;
            bands = params.bands;

            // Make AJAX call here to getAngles with filename & entry as args
            // to get the upAngle and northAngle values
            $http({
                method: 'GET',
                //url: '/o2/imageSpace/getAngles',
                url: AppO2.APP_CONFIG.params.imageSpace.baseUrl + '/getAngles',
                params: {
                    filename: filename,
                    entry: entry
                }
            }).then(function successCallback(response) {
                // this callback will be called asynchronously
                // when the response is available
                upAngle = response.data.upAngle;
                northAngle = response.data.northAngle;

            }, function errorCallback(response) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });

            var crossOrigin = 'anonymous';
            imgCenter = [imgWidth / 2, -imgHeight / 2];

            // Maps always need a projection, but Zoomify layers are not geo-referenced, and
            // are only measured in pixels.  So, we create a fake projection that the map
            // can use to properly display the layer.
            proj = new ol.proj.Projection({
                code: 'ImageSpace',
                units: 'pixels',
                extent: [0, 0, imgWidth, imgHeight]
            });

            source = new ImageSpace({
                url: AppO2.APP_CONFIG.params.imageSpace.baseUrl + '/getTile',
                filename: filename,
                entry: entry,
                format: 'jpeg',
                size: [imgWidth, imgHeight],
                crossOrigin: crossOrigin,
                numOfBands: numOfBands,
                bands: bands
            });

            source2 = new ImageSpace({
                url: AppO2.APP_CONFIG.params.imageSpace.baseUrl + '/getTileOverlay',
                filename: filename,
                entry: entry,
                format: 'png',
                size: [imgWidth, imgHeight],
                crossOrigin: crossOrigin
            });

            var interactions = ol.interaction.defaults({
                altShiftDragRotate: true
            });

            // create full screen control
            var span = document.createElement('span');
            span.className = 'glyphicon glyphicon-fullscreen';
            var fullScreenControl = new ol.control.FullScreen({
                label: span
            });

            map = new ol.Map({
                controls: ol.control.defaults().extend([
                    new RotateNorthControl(),
                    new RotateUpControl(),
                    fullScreenControl
                ]),
                //interactions: ol.interaction.defaults().extend([
                //    new ol.interaction.DragRotateAndZoom()
                //]),
                interactions: interactions,
                layers: [
                    new ol.layer.Tile({
                        source: source
                    })
                ],
                logo: false,
                target: 'imageMap',
                view: new ol.View({
                    projection: proj,
                    center: imgCenter,
                    zoom: 2,
                    // constrain the center: center cannot be set outside
                    // this extent
                    extent: [0, -imgHeight, imgWidth, 0]
                })
            });

            //Beginning - Band Selections Section

            this.getImageBands = function(){
              var bandVal = bands.split( ',' );

              if ( bandVal.length > 0 ) {
                if ( bandVal[0] != 'default' ) {
                  if ( numOfBands <= 1 ) {
                    bands = bandVal[0];
                  }else {
                    if ( numOfBands == 2 ){
                      bands = '1,2';
                    }else{
                      bands = bandVal[0];
                    }
                    for ( var bandNum = 1; bandNum < numOfBands; bandNum++ ) {
                      if ( bandVal[bandNum] ) {
                        bands = bands + ',' + bandVal[bandNum];
                      }
                    }
                  }
                }else {
                  bands = 'default';
                  //var newNum;
                  //for ( var bandNum2 = 1; bandNum2 < numOfBands; bandNum2++ ) {
                    //newNum = bandNum2 + 1;
                    //bands = bands + ',' + newNum;
                  //}
                }
              }
                this.bands = bands;
                this.numOfBands = numOfBands;
            };

            this.setBands = function(bandsVal){
              bands = bandsVal;
              source.refresh();
            };

            //END - Band Selection Section

            map.render('imageMap');

            // hide the default north arrow
            $('.ol-rotate').removeClass('ol-rotate');
            // rotate the custom north arrow according to the view
            rotateNorthArrow(northAngle);
            map.getView().on('change:rotation', function(e) {
                var rotation = e.target.get(e.key) + northAngle;
                rotateNorthArrow(rotation);
            });
        };
    }
}());
