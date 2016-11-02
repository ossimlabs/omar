(function() {
    'use strict';
    angular
        .module('omarApp')
        .service('imageSpaceService', ['$rootScope', '$http', 'stateService', '$timeout', imageSpaceService]);

    function imageSpaceService($rootScope, $http, stateService, $timeout) {

      // #################################################################################
      // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
      // provides access to various client params in application.yml
      // #################################################################################
      //console.log('AppO2.APP_CONFIG in imageSpaceService: ', AppO2.APP_CONFIG);

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

      // Measurement variables

      var measureSource = new ol.source.Vector();

      var vector = new ol.layer.Vector({
        source: measureSource,
        style: new ol.style.Style({
          fill: new ol.style.Fill({
            color: 'rgba(255, 255, 255, 0.3)'
          }),
          stroke: new ol.style.Stroke({
            color: 'cyan',
            width: 3
          }),
          image: new ol.style.Circle({
            radius: 0,
            fill: new ol.style.Fill({
              color: 'cyan'
            })
          })
        })
      });

      var type,
          sketch,
          draw,
          helpTooltipElement,
          helpTooltip,
          measureTooltipElement,
          measureTooltip

      var continuePolygonMsg = 'Click to continue drawing the polygon';
      var continueLineMsg = 'Click to continue drawing the line';

      // end Measurement variables

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

          // Sets header title
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
              url: AppO2.APP_CONFIG.params.imageSpace.baseUrl + '/getAngles',
              params: {
                filename: filename,
                entry: entry
              }
            }).then(function successCallback(response) {

              upAngle = response.data.upAngle;
              northAngle = response.data.northAngle;

            }, function errorCallback(response) {

              console.error(response);

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

            // Create full screen control
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
              interactions: interactions,
              layers: [
                new ol.layer.Tile({
                  source: source
                }),
                vector
              ],
              logo: false,
              target: 'imageMap',
              view: new ol.View({
                projection: proj,
                center: imgCenter,
                zoom: 3,
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
                } else {
                  bands = 'default';

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





























            // ############# Measure things #######################

            var pointerMoveHandler = function(evt) {

              if (evt.dragging) {
                return;
              }
              var helpMsg = '<div class="text-center">Single-click to start measuring. </br>  Double-click to end.</div>';

              if (sketch) {
                var geom = (sketch.getGeometry());

                if (geom instanceof ol.geom.Polygon) {

                  helpMsg = continuePolygonMsg;

                } else if (geom instanceof ol.geom.LineString) {

                  helpMsg = continueLineMsg;

                }

              }

              helpTooltipElement.innerHTML = helpMsg;
              helpTooltip.setPosition(evt.coordinate);

              helpTooltipElement.classList.remove('hidden');
            };

            //map.on('pointermove', pointerMoveHandler);

            map.getViewport().addEventListener('mouseout', function() {

              helpTooltipElement.classList.add('hidden');

            });

            var formatLength = function(line) {
              var length;
              if (geodesicCheckbox.checked) {
                var coordinates = line.getCoordinates();
                length = 0;
                var sourceProj = map.getView().getProjection();
                for (var i = 0, ii = coordinates.length - 1; i < ii; ++i) {
                  var c1 = ol.proj.transform(coordinates[i], sourceProj, 'EPSG:4326');
                  var c2 = ol.proj.transform(coordinates[i + 1], sourceProj, 'EPSG:4326');
                  length += wgs84Sphere.haversineDistance(c1, c2);
                }
              } else {
                length = Math.round(line.getLength() * 100) / 100;
              }
              var output;
              if (length > 100) {
                output = (Math.round(length / 1000 * 100) / 100) +
                    ' ' + 'km';
              } else {
                output = (Math.round(length * 100) / 100) +
                    ' ' + 'm';
              }
              return output;
            };

            var formatArea = function(polygon) {
              var area;
              if (geodesicCheckbox.checked) {
                var sourceProj = map.getView().getProjection();
                var geom = /** @type {ol.geom.Polygon} */(polygon.clone().transform(
                    sourceProj, 'EPSG:4326'));
                var coordinates = geom.getLinearRing(0).getCoordinates();
                area = Math.abs(wgs84Sphere.geodesicArea(coordinates));
              } else {
                area = polygon.getArea();
              }
              var output;
              if (area > 10000) {
                output = (Math.round(area / 1000000 * 100) / 100) +
                    ' ' + 'km<sup>2</sup>';
              } else {
                output = (Math.round(area * 100) / 100) +
                    ' ' + 'm<sup>2</sup>';
              }
              return output;
            };




            function addMeasureInteraction(measureType) {

              //console.log('addInteraction...');
              //var type = (typeSelect.value == 'area' ? 'Polygon' : 'LineString');
              var type = measureType;

              draw = new ol.interaction.Draw({
                source: measureSource,
                type: /** @type {ol.geom.GeometryType} */ (type),
                style: new ol.style.Style({
                  fill: new ol.style.Fill({
                    color: 'rgba(255, 255, 255, 0.2)'
                  }),
                  stroke: new ol.style.Stroke({
                    //color: 'rgba(0, 0, 0, 0.5)',
                    color: 'rgba(0,255,255, 1.0)',
                    lineDash: [10, 10],
                    width: 3
                  }),
                  image: new ol.style.Circle({
                    radius: 5,
                    stroke: new ol.style.Stroke({
                      color: 'rgba(0, 0, 0, 0.7)'
                    }),
                    fill: new ol.style.Fill({
                      color: 'rgba(255, 255, 255, 0.2)'
                    })
                  })
                })

              });
              //map.addInteraction(draw);

              createMeasureTooltip();
              createHelpTooltip();

              var listener;
              draw.on('drawstart',
                function(evt) {

                  measureSource.clear();

                  // set sketch
                  sketch = evt.feature;

                  /** @type {ol.Coordinate|undefined} */
                  var tooltipCoord = evt.coordinate;

                  listener = sketch.getGeometry().on('change', function(evt) {
                    var geom = evt.target;

                    var output;
                    if (geom instanceof ol.geom.Polygon) {

                      //output = formatArea(geom);
                      tooltipCoord = geom.getInteriorPoint().getCoordinates();

                    } else if (geom instanceof ol.geom.LineString) {

                      //output = formatLength(geom);
                      tooltipCoord = geom.getLastCoordinate();

                    }

                    //measureTooltipElement.innerHTML = output;
                    measureTooltip.setPosition(tooltipCoord);

                  });
                }, this);

              draw.on('drawend',
                function() {

                  console.log('sketch', sketch)
                  var sketchGeom = sketch.getGeometry();

                  console.log('sketch geometry: ', sketch.getGeometry().getType());
                  var sketchArray = sketch.values_.geometry.flatCoordinates;
                  //console.log('sketchArray: ', sketchArray);

                  // We need to map over the items in the sketchArray, and
                  // multiply every other item (the y value on the OL3 grid) by -1
                  // before we pass this to the mensa service.  Mensa expects the
                  // XY to start in the upper-left.  OL3 starts in the lower-left.
                  var sketchString = sketchArray.map(function(el, index){

                    return index %2 ? el * -1 : el;

                  }).join(" ").match(/[+-]?\d+(\.\d+)?\s+[+-]?\d+(\.\d+)?/g).join(", ");

                  // Logic for type of geometry on sketch to set the type
                  // of array we need to send to the mensa service
                  if (sketchGeom instanceof ol.geom.LineString) {

                    //console.log('LineString present...');
                    var wktArray =  'LINESTRING(' + sketchString + ')';

                  }
                  else {

                    //console.log('Polygon...');
                    var wktArray =  'POLYGON((' + sketchString + '))';

                  }

                  var mensaUrl = AppO2.APP_CONFIG.params.mensaApp.baseUrl + '/imageDistance?';

                  var measureOutput;

                  $http({

                    method: 'POST',
                    url: encodeURI(mensaUrl),
                    params: {
                      filename: filename,
                      entryId: entry,
                      pointList: wktArray
                    }

                  }).then(function(response) {

                      console.log(response.data.data);

                      var data;
                      data = response.data.data;

                      //measureTooltipElement.className = 'tooltip tooltip-static';
                      //measureTooltip.setOffset([0, -7]);

                      //measureOutput = 'Rect Dist: ' + response.data.data.distance + ' m'
                      //  + '</br>' + 'Geodetic Dist: ' + response.data.data.distance + ' m';

                      //measureTooltipElement.innerHTML = measureOutput;

                      // unset tooltip so that a new one can be created
                      //measureTooltipElement = null;
                      //createMeasureTooltip();

                      // $timeout needed: http://stackoverflow.com/a/18996042
                      $timeout(function() {

                        $rootScope.$broadcast('measure: updated', data);

                      });

                      ol.Observable.unByKey(listener);


                  }, function errorCallback(response) {

                      console.log(response);

                  });

                  //measureTooltipElement.className = 'tooltip tooltip-static';
                  //measureTooltip.setOffset([0, -7]);

                  //unset sketch
                  sketch = null;

                  // unset tooltip so that a new one can be created
                  // measureTooltipElement = null;
                  // createMeasureTooltip();
                  // ol.Observable.unByKey(listener);

                }, this);

                 // Creates a new help tooltip
                function createHelpTooltip() {
                  if (helpTooltipElement) {
                    helpTooltipElement.parentNode.removeChild(helpTooltipElement);
                  }
                  helpTooltipElement = document.createElement('div');
                  helpTooltipElement.className = 'tooltip hidden';
                  helpTooltip = new ol.Overlay({
                    element: helpTooltipElement,
                    offset: [15, 0],
                    positioning: 'center-left'
                  });
                  map.addOverlay(helpTooltip);
                }

                // Adds the measurement data to the overlay bubble
                function createMeasureTooltip() {
                  if (measureTooltipElement) {
                    measureTooltipElement.parentNode.removeChild(measureTooltipElement);
                  }
                  measureTooltipElement = document.createElement('div');
                  measureTooltipElement.className = 'tooltip tooltip-measure';
                  measureTooltip = new ol.Overlay({
                    element: measureTooltipElement,
                    offset: [0, -15],
                    positioning: 'bottom-center'
                  });
                  //map.addOverlay(measureTooltip);
                }

            }






            this.measureActivate = function(measureType) {

              // Remove the draw interaction if it is present (resets it)
              map.removeInteraction(draw);

              // Set the desired measurement type (Polygon or LineString)
              addMeasureInteraction(measureType);

              // Add the draw interaction for aour measurement
              map.addInteraction(draw);

              map.on('pointermove', pointerMoveHandler);

            }

            this.measureClear = function() {

              console.log('measure clear');
              map.removeInteraction(draw);

              map.un('pointermove', pointerMoveHandler);

              measureSource.clear();

            }


        };

    }
}());
