//= require jquery-2.2.0.min.js
//= require webjars/bootswatch-superhero/3.3.5/js/bootstrap.js
//= require omar-openlayers.js
//= require_self

var ossimtools = (function ()
{
    "use strict";

    var center_lat, center_lon, radiusROI,
        radiusLZ, roughness, slope,
        fovStart, fovStop, heightOfEye,
        gainFactor, 
        sunAz, sunEl,
        map, layers, extent;


    function updateHLZ()
    {
        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) == 'hlz' )
            {
                // Do with layer
                var source = layer.getSource();
                var params = source.getParams();
                params.lat = center_lat;
                params.lon = center_lon;
                params.radiusROI = radiusROI;
                params.radiusLZ = radiusLZ;
                params.roughness = roughness;
                params.slope = slope;
                source.updateParams( params );
            }
        } );
    }

    function updateViewshed()
    {
        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) == 'viewshed' )
            {
                // Do with layer
                var source = layer.getSource();
                var params = source.getParams();
                params.lat = center_lat;
                params.lon = center_lon;
                params.radiusROI = radiusROI;
                params.fovStart = fovStart;
                params.fovStop = fovStop;
                params.heightOfEye = heightOfEye;
                source.updateParams( params );
            }
        } );
    }

    function updateSlope()
    {
        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) == 'slope' )
            {
                // Do with layer
                var source = layer.getSource();
                var params = source.getParams();
                params.lat = center_lat;
                params.lon = center_lon;
                params.radiusROI = radiusROI;
                params.gainFactor = gainFactor;
                source.updateParams( params );
            }
        } );
    }

    function updateHillshade()
    {
        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) == 'hillshade' )
            {
                // Do with layer
                var source = layer.getSource();
                var params = source.getParams();
                params.lat = center_lat;
                params.lon = center_lon;
                params.radiusROI = radiusROI;
                params.sunAz = sunAz;
                params.sunEl = sunEl;
                source.updateParams( params );
            }
        } );
    }

    function toggleLayer( name, status )
    {
        console.log( name, status );

        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) === name )
            {
                layer.set( 'visible', status );
            }
        } );
    }

    function setOverlayOpacity( name, value )
    {
        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) === name )
            {
                layer.setOpacity( value );
            }
        } );
    }

    function onMoveEnd( evt )
    {
         var center = ol.proj.transform(map.getView().getCenter(), 'EPSG:3857', 'EPSG:4326')
         extent = map.getView().calculateExtent(map.getSize())
         
        //console.log( center );

        center_lat = center[1];
        center_lon = center[0];

        $( '#lat' ).val( center_lat );
        $( '#lon' ).val( center_lon );

        updateHLZ();
        updateViewshed();
        updateSlope();
        updateHillshade();

    }

    function initialize( initParams )
    {
        center_lat = initParams.lat;
        center_lon = initParams.lon;
        radiusROI = initParams.radiusROI;
        radiusLZ = initParams.radiusLZ;
        roughness = initParams.roughness;
        slope = initParams.slope;
        fovStart = initParams.fovStart;
        fovStop = initParams.fovStop;
        heightOfEye = initParams.heightOfEye;
        extent = initParams.extent;
     
        layers = [
            new ol.layer.Tile( {
                name: 'reference',
                 source: new ol.source.OSM()
            } ),
            new ol.layer.Image( {
                name: 'hlz',
                source: new ol.source.ImageWMS( {
                    url: '/ossimTools/renderHLZ',
                    params: {
                        visible: false,
                        LAYERS: '',
                        VERSION: '1.1.1',
                        lat: center_lat,
                        lon: center_lon,
                        radiusROI: radiusROI,
                        radiusLZ: radiusLZ,
                        roughness: roughness,
                        slope: slope
                    }
                } )
            } ),
            new ol.layer.Tile( {
                name: 'hillshade',
                source: new ol.source.TileWMS( {
                    url: '/ossimTools/renderHillShade',
                    params: {
                        visible: false,
                        VERSION: '1.1.1'
                    }
                } )
            } ),
            new ol.layer.Image( {
                name: 'viewshed',
                source: new ol.source.ImageWMS( {
                    url: '/ossimTools/renderViewshed',
                    params: {
                        visible: false,
                        LAYERS: '',
                        VERSION: '1.1.1',
                        lat: center_lat,
                        lon: center_lon,
                        radiusROI: radiusROI,
                        fovStart: fovStart,
                        fovStop: fovStop,
                        heightOfEye: heightOfEye
                    }
                } )
            } ),
        ];

        map = new ol.Map( {
             controls: ol.control.defaults().extend( [
                 new ol.control.ScaleLine( {
                     units: 'meters'
                 } )
             ] ),
             layers: layers,
             target: 'map',
             view: new ol.View( {
                 //projection: 'EPSG:4326'//,
                 projection: 'EPSG:3857'//,
                 //center: [initParams.lon, initParams.lat],
                 //zoom: 2
             } )
         } );

        map.on( 'moveend', onMoveEnd );

                console.log( initParams );

        extent = ol.extent.boundingExtent( initParams.extent );

        map.getView().fit( extent, map.getSize() );

        $( '#lat' ).val( center_lat );
        $( '#lon' ).val( center_lon );
        $( '#radiusROI' ).val( radiusROI );
        $( '#radiusLZ' ).val( radiusLZ );
        $( '#roughness' ).val( roughness );
        $( '#slope' ).val( slope );
        $( '#fovStart' ).val( fovStart );
        $( '#fovStop' ).val( fovStop );
        $( '#heightOfEye' ).val( heightOfEye );
        $( '#gainFactor' ).val( gainFactor );
        $( '#sunAz' ).val( sunAz );
        $( '#sunEl' ).val( sunEl );

        $( '#toggleHLZ' ).click( function ()
        {
            var $this = $( this );
            // $this will contain a reference to the checkbox
            if ( $this.is( ':checked' ) )
            {
                // the checkbox was checked
                toggleLayer( 'hlz', true );
            }
            else
            {
                // the checkbox was unchecked
                toggleLayer( 'hlz', false );
            }
        } );

        $( '#toggleViewshed' ).click( function ()
        {
            var $this = $( this );
            // $this will contain a reference to the checkbox
            if ( $this.is( ':checked' ) )
            {
                // the checkbox was checked
                toggleLayer( 'viewshed', true );
            }
            else
            {
                // the checkbox was unchecked
                toggleLayer( 'viewshed', false );
            }
        } );

        $( '#toggleSlope' ).click( function ()
        {
            var $this = $( this );
            // $this will contain a reference to the checkbox
            if ( $this.is( ':checked' ) )
            {
                // the checkbox was checked
                toggleLayer( 'slope', true );
            }
            else
            {
                // the checkbox was unchecked
                toggleLayer( 'slope', false );
            }
        } );


        $( '#toggleHillshade' ).click( function ()
        {
            var $this = $( this );
            // $this will contain a reference to the checkbox
            if ( $this.is( ':checked' ) )
            {
                // the checkbox was checked
                toggleLayer( 'hillshade', true );
            }
            else
            {
                // the checkbox was unchecked
                toggleLayer( 'hillshade', false );
            }
        } );


        $( '#submitButton' ).on( 'click', function ( e )
        {
            // Common Parameters:
            center_lat = $( '#lat' ).val();
            center_lon = $( '#lon' ).val();
            radiusROI = $( '#radiusROI' ).val();

            // HLZ Parameters:
            radiusLZ = $( '#rlz' ).val();
            roughness = $( '#roughness' ).val();
            slope = $( '#slope' ).val();

            // Viewshed Parameters:

            fovStart = $( '#fovStart' ).val();
            fovStop = $( '#fovStop' ).val();
            heightOfEye = $( '#heightOfEye' ).val();

            // Slope Parameters:
            gainFactor = $( '#gainFactor' ).val();
            
            // Hillshade Parameters:
            sunAz = $( '#sunAz' ).val();
            sunEl = $( '#sunEl' ).val();
            
            map.getView().setCenter( ol.proj.transform([center_lon, center_lat], 'EPSG:4326', 'EPSG:3857') );
            extent = map.getView().calculateExtent(map.getSize()); 
            
            // Pass the request to OSSIM:
            updateHLZ();
            updateViewshed();
            updateSlope();
            updateHillshade();
            

        } );

        setOverlayOpacity( 'hlz', 0.5 );
        setOverlayOpacity( 'viewshed', 0.5 );
        setOverlayOpacity( 'slope', 0.5 );
        setOverlayOpacity( 'hillshade', 0.5 );

    }

    return {
        initialize: initialize
    };
})();
