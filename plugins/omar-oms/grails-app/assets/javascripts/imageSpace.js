/**
 * Created by sbortman on 12/10/15.
 */
//= require jquery-2.2.0.min.js
//= require omar-openlayers.js
//= require_self

var ImageSpace = (function ()
{
    'use strict';

    var filename;
    var entry;
    var imgWidth;
    var imgHeight;
    var upAngle;
    var northAngle;


    var RotateNorthControl = function ( opt_options )
    {

        var options = opt_options || {};
        var button = document.createElement( 'button' );

        button.innerHTML = 'N';

        var this_ = this;

        var handleRotateNorth = function ( e )
        {
            this_.getMap().getView().setRotation( northAngle );
            console.log( 'handleRotateNorth', northAngle );
        };

        button.addEventListener( 'click', handleRotateNorth, false );
        button.addEventListener( 'touchstart', handleRotateNorth, false );

        var element = document.createElement( 'div' );

        element.className = 'rotate-north ol-unselectable ol-control';
        element.appendChild( button );

        ol.control.Control.call( this, {
            element: element,
            target: options.target
        } );

    };
    ol.inherits( RotateNorthControl, ol.control.Control );

    var RotateUpControl = function ( opt_options )
    {

        var options = opt_options || {};
        var button = document.createElement( 'button' );

        button.innerHTML = 'U';

        var this_ = this;

        var handleRotateUp = function ( e )
        {
            console.log( 'handleRotateUp', upAngle );
            this_.getMap().getView().setRotation( upAngle );
        };

        button.addEventListener( 'click', handleRotateUp, false );
        button.addEventListener( 'touchstart', handleRotateUp, false );

        var element = document.createElement( 'div' );

        element.className = 'rotate-up ol-unselectable ol-control';
        element.appendChild( button );

        ol.control.Control.call( this, {
            element: element,
            target: options.target
        } );

    };
    ol.inherits( RotateUpControl, ol.control.Control );


    var ImageSpaceTierSizeCalculation = {
        DEFAULT: 'default',
        TRUNCATED: 'truncated'
    };


    var ImageSpace = function ( opt_options )
    {
        var options = opt_options || {};

        var size = options.size;
        var tierSizeCalculation = options.tierSizeCalculation !== undefined ?
            options.tierSizeCalculation :
            ImageSpaceTierSizeCalculation.DEFAULT;

        var filename = options.filename;
        var entry = options.entry;
        var format = options.format;

        var imageWidth = size[0];
        var imageHeight = size[1];
        var tierSizeInTiles = [];
        var tileSize = ol.DEFAULT_TILE_SIZE || 256;

        switch ( tierSizeCalculation )
        {
        case ImageSpaceTierSizeCalculation.DEFAULT:
            while ( imageWidth > tileSize || imageHeight > tileSize )
            {
                tierSizeInTiles.push( [
                    Math.ceil( imageWidth / tileSize ),
                    Math.ceil( imageHeight / tileSize )
                ] );
                tileSize += tileSize;
            }
            break;
        case ImageSpaceTierSizeCalculation.TRUNCATED:
            var width = imageWidth;
            var height = imageHeight;
            while ( width > tileSize || height > tileSize )
            {
                tierSizeInTiles.push( [
                    Math.ceil( width / tileSize ),
                    Math.ceil( height / tileSize )
                ] );
                width >>= 1;
                height >>= 1;
            }
            break;
        default:
            goog.asserts.fail();
            break;
        }

        tierSizeInTiles.push( [1, 1] );
        tierSizeInTiles.reverse();

        //console.log( 'tierSizeInTiles', tierSizeInTiles );

        var resolutions = [1];
        var tileCountUpToTier = [0];
        var i = 1,
            ii = tierSizeInTiles.length;
        //for ( i = 1, ii = tierSizeInTiles.length; i < ii; i++ )
        while ( i < ii )
        {
            resolutions.push( 1 << i );
            tileCountUpToTier.push(
                tierSizeInTiles[i - 1][0] * tierSizeInTiles[i - 1][1] +
                tileCountUpToTier[i - 1]
            );
            i++
        }

        resolutions.reverse();
        //console.log( 'resolutions', resolutions );

        var extent = [0, -size[1], size[0], 0];
        var tileGrid = new ol.tilegrid.TileGrid( {
            extent: extent,
            origin: ol.extent.getTopLeft( extent ),
            resolutions: resolutions
        } );

        var url = options.url;

        /**
         * @this {ol.source.TileImage}
         * @param {ol.TileCoord} tileCoord Tile Coordinate.
         * @param {number} pixelRatio Pixel ratio.
         * @param {ol.proj.Projection} projection Projection.
         * @return {string|undefined} Tile URL.
         */
        function tileUrlFunction( tileCoord, pixelRatio, projection )
        {
            if ( !tileCoord )
            {
                return undefined;
            }
            else
            {
                var tileZ = tileCoord[0];
                var tileX = tileCoord[1];
                var tileY = -tileCoord[2] - 1;

                //console.log( tileCoord, [tileZ, tileX, tileY] );

                return url + '?filename=' + filename + '&entry=' + entry + '&z=' + tileZ
                    + '&x=' + tileX + '&y=' + tileY + '&format=' + format;
            }
        }

        ol.source.TileImage.call( this, {
            attributions: options.attributions,
            crossOrigin: options.crossOrigin,
            logo: options.logo,
            reprojectionErrorThreshold: options.reprojectionErrorThreshold,
            tileClass: ol.source.ZoomifyTile,
            tileGrid: tileGrid,
            tileUrlFunction: tileUrlFunction
        } );
    };

    ol.inherits( ImageSpace, ol.source.TileImage );

    function init( initParams )
    {
        // This server does not support CORS, and so is incompatible with WebGL.
        //var imgWidth = 8001;
        //var imgHeight = 6943;
        //var url = 'http://mapy.mzk.cz/AA22/0103/';
        //var crossOrigin = undefined;

        filename = initParams.filename;
        entry = initParams.entry;
        imgWidth = initParams.imgWidth;
        imgHeight = initParams.imgHeight;
        upAngle = initParams.upAngle;
        northAngle = initParams.northAngle;

        var crossOrigin = 'anonymous';
        var imgCenter = [imgWidth / 2, -imgHeight / 2];

        // Maps always need a projection, but Zoomify layers are not geo-referenced, and
        // are only measured in pixels.  So, we create a fake projection that the map
        // can use to properly display the layer.
        var proj = new ol.proj.Projection( {
            code: 'ImageSpace',
            units: 'pixels',
            extent: [0, 0, imgWidth, imgHeight]
        } );

        var source = new ImageSpace( {
            url: '/imageSpace/getTile',
            filename: filename,
            entry: entry,
            format: 'jpeg',
            size: [imgWidth, imgHeight],
            crossOrigin: crossOrigin
        } );

        var source2 = new ImageSpace( {
            url: '/imageSpace/getTileOverlay',
            filename: filename,
            entry: entry,
            format: 'png',
            size: [imgWidth, imgHeight],
            crossOrigin: crossOrigin
        } );

        var map = new ol.Map( {
            controls: ol.control.defaults().extend( [
                new RotateNorthControl(),
                new RotateUpControl()
            ] ),
            layers: [
                new ol.layer.Tile( {
                    source: source
                } ),
                new ol.layer.Tile( {
                    source: source2
                } )
            ],
            target: 'map',
            view: new ol.View( {
                projection: proj,
                center: imgCenter,
                zoom: 0,
                // constrain the center: center cannot be set outside
                // this extent
                extent: [0, -imgHeight, imgWidth, 0]
            } )
        } );
    }

    return {
        init: init
    };
})();
