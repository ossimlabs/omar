/**
 * Created by sbortman on 11/13/15.
 */
//= require jquery-2.1.3.js
//= require webjars/bootswatch-superhero/3.3.5/js/bootstrap.js
//= require webjars/openlayers/3.13.0/ol.js
//= require_self

ossimHLZ = (function ()
{
    "use strict";

    var lat, lon,
        radiusROI, radiusLZ, roughness, slope,
        fovStart, fovStop, heightOfEye,
        map, layers;


    function updateHLZ()
    {
        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) == 'hlz' )
            {
                // Do with layer
                var source = layer.getSource();
                var params = source.getParams();
                params.lat = lat;
                params.lon = lon;
                params.radiusROI = radiusROI;
                params.radiusLZ = radiusLZ;
                //params.roughness = roughness;
                params.slope = slope;

                source.updateParams( params );
            }
        } );
    }

    function updateVS()
    {
        map.getLayers().forEach( function ( layer )
        {
            if ( layer.get( 'name' ) == 'ovs' )
            {
                // Do with layer
                var source = layer.getSource();
                var params = source.getParams();
                params.lat = lat;
                params.lon = lon;
                params.radius = radiusROI;
                params.fovStart = fovStart;
                params.fovStop = fovStop;
                params.heightOfEye = heightOfEye;

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
        var center = map.getView().getCenter();

        //console.log( center );

        lat = center[1];
        lon = center[0];

        $( '#lat' ).val( lat );
        $( '#lon' ).val( lon );

        updateHLZ();
        updateVS();

    }

    function initialize( initParams )
    {
        lat = initParams.lat;
        lon = initParams.lon;
        radiusROI = initParams.radiusROI;

        radiusLZ = initParams.radiusLZ;
        //roughness = initParams.roughness;
        slope = initParams.slope;

        fovStart = initParams.fovStart;
        fovStop = initParams.fovStop;
        heightOfEye = initParams.heightOfEye;

        layers = [
            new ol.layer.Tile( {
                name: 'reference',
//                source: new ol.source.TileWMS( {
//                    url: 'http://geoserver-demo01.dev.ossim.org/geoserver/ged/wms?',
//                    params: {
//                        LAYERS: 'osm-group'
//                    }
//                } )
                source: new ol.source.OSM()
            } ),
            new ol.layer.Tile( {
                name: 'hillshade',
                source: new ol.source.TileWMS( {
                    url: '/hlz/renderHillShade',
                    params: {
                        VERSION: '1.1.1'
                    }
                } )
            } ),
//            new ol.layer.Image( {
//                name: 'hlz',
//                source: new ol.source.ImageWMS( {
//                    url: '/hlz/renderHLZ',
//                    params: {
//                        LAYERS: '',
//                        VERSION: '1.1.1',
//                        lat: lat,
//                        lon: lon,
//                        radiusROI: radiusROI,
//                        radiusLZ: radiusLZ,
//                        //roughness: roughness,
//                        slope: slope
//                    }
//                } )
//            } ),
            new ol.layer.Image( {
                name: 'ovs',
                source: new ol.source.ImageWMS( {
                    url: '/hlz/renderVS',
                    params: {
                        LAYERS: '',
                        VERSION: '1.1.1',
                        lat: lat,
                        lon: lon,
                        radius: radiusROI,
                        fovStart: fovStart,
                        fovStop: fovStop,
                        heightOfEye: heightOfEye
                    }
                } )
            } )
        ];

        map = new ol.Map( {
            controls: ol.control.defaults().extend( [
                new ol.control.ScaleLine( {
                    units: 'degrees'
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
        var extent = ol.extent.boundingExtent( initParams.extent );
        //console.log( extent );
        map.getView().fit( extent, map.getSize() );

        $( '#lat' ).val( lat );
        $( '#lon' ).val( lon );
        $( '#radiusROI' ).val( radiusROI );
        $( '#radiusLZ' ).val( radiusLZ );
        //$( '#roughness' ).val( roughness );
        $( '#slope' ).val( slope );
        $( '#fovStart' ).val( fovStart );
        $( '#fovStop' ).val( fovStop );
        $( '#heightOfEye' ).val( heightOfEye );


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

        $( '#toggleVS' ).click( function ()
        {
            var $this = $( this );
            // $this will contain a reference to the checkbox
            if ( $this.is( ':checked' ) )
            {
                // the checkbox was checked
                toggleLayer( 'ovs', true );
            }
            else
            {
                // the checkbox was unchecked
                toggleLayer( 'ovs', false );
            }
        } );


        $( '#updateHLZ' ).on( 'click', function ( e )
        {
            lat = $( '#lat' ).val();
            lon = $( '#lon' ).val();
            radiusROI = $( '#radiusROI' ).val();

            radiusLZ = $( '#radiusLZ' ).val();
            //roughness = $( '#roughness' ).val();
            slope = $( '#slope' ).val();


            fovStart = $( '#fovStart' ).val();
            fovStop = $( '#fovStop' ).val();
            heightOfEye = $( '#heightOfEye' ).val();


            updateHLZ();
            updateVS();


            console.log(map.getView().calculateExtent(map.getSize()));

            map.getView().setCenter( [lon, lat] );
        } );

        setOverlayOpacity( 'hlz', 0.5 );
        setOverlayOpacity( 'ovs', 0.5 );

    }

    return {
        initialize: initialize
    };
})();
