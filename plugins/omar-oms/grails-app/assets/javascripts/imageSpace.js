/**
 * Created by sbortman on 11/30/15.
 */
//= require jquery-2.1.3.js
//= require webjars/openlayers/3.11.1/ol.js
//= require_self

var ImageSpace = (function ()
{
    'use strict';

    function initialize( imageModel )
    {
        var filename = imageModel.filename;
        var imageWidth = imageModel.imageWidth;
        var imageHeight = imageModel.imageHeight;
        var start = imageModel.start, stop = imageModel.stop;

        var tileSize = 256;

        imageWidth = Math.ceil( imageWidth / tileSize ) * tileSize;
        imageHeight = Math.ceil( imageHeight / tileSize ) * tileSize;


        console.log(imageWidth / 256, imageHeight / 256);

        var projection = new ol.proj.Projection( {

            code: 'IMAGESPACE',
            units: 'pixels',

            extent: [0, 0, imageWidth, imageHeight]
        } );

        var projectionExtent = projection.getExtent();
        var maxResolution = ol.extent.getWidth( projectionExtent ) / tileSize;
        var resolutions = [];

        for ( var z = start; z <= stop; z++ )
        {
            resolutions[z] = maxResolution / Math.pow( 2, z );

        }

        //console.log(resolutions);

        var map = new ol.Map( {

            target: 'map',
            layers: [
                new ol.layer.Tile( {
                    source: new ol.source.TileImage( {
                        tileUrlFunction: function ( tileCoord, pixelRatio, projection )
                        {
                            var z = tileCoord[0];
                            var x = tileCoord[1];
                            var y = -tileCoord[2] - 1;
                            return '/imageSpace/getTile?filename=' + filename + '&z=' + z + '&x=' + x + '&y=' + y + '&format=gif';
                        },
                        projection: projection,
                        tileGrid: new ol.tilegrid.TileGrid( {
                            origin: ol.extent.getTopLeft( projectionExtent ),
                            resolutions: resolutions,
                            tileSize: tileSize
                        } )
                    } ),
                    extent: projectionExtent
                } ),
                new ol.layer.Tile( {
                    source: new ol.source.TileImage( {
                        tileUrlFunction: function ( tileCoord, pixelRatio, projection )
                        {
                            var z = tileCoord[0];
                            var x = tileCoord[1];
                            var y = -tileCoord[2] - 1;
                            return '/imageSpace/getTileOverlay?z=' + z + '&x=' + x + '&y=' + y + '&format=png';
                        },
                        projection: projection,
                        tileGrid: new ol.tilegrid.TileGrid( {
                            origin: ol.extent.getTopLeft( projectionExtent ),
                            resolutions: resolutions,
                            tileSize: tileSize
                        } )
                    } ),
                    extent: projectionExtent
                } )
            ],
            view: new ol.View( {
                projection: projection,
                center: [imageWidth / 2, imageHeight / 2],
                zoom: start,
                minZoom: start,
                maxZoom: stop,
                extent: projectionExtent
            } )
        } );

        map.on( 'click', function ( e )
        {
            console.log( e.coordinate );
        } );
    }

    return {
        initialize: initialize
    };
})();